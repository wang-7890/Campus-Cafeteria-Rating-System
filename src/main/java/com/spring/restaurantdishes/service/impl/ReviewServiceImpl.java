package com.spring.restaurantdishes.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.DishMapper;
import com.spring.restaurantdishes.Mapper.ReviewMapper;
import com.spring.restaurantdishes.model.RequestDetails.ReviewRequest;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.dataBasePojo.Dish;
import com.spring.restaurantdishes.model.dataBasePojo.review;
import com.spring.restaurantdishes.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 发表评价（接口文档 5.1）
 * 1. 事务内：锁菜品行 -> 查重 -> 插入评价 -> 重算菜品统计
 * 2. 事务提交后：删除菜品详情缓存 + 覆盖写热门榜
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private static final String DISH_DETAIL_CACHE_PREFIX = "canteen:dish:detail:";
    private static final String HOT_DISHES_KEY = "canteen:hot:dishes";

    private static final String CONTENT_INVALID_MESSAGE = "content: 评价内容去首尾空格后需为2～500个字符";
    private static final String DISH_NOT_FOUND_MESSAGE = "菜品不存在或已下架";
    private static final String DUPLICATE_REVIEW_MESSAGE = "你已经评价过该菜品，可以修改原评价";

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public Result<Map<String, Long>> createReview(Long dishId, Long userId, ReviewRequest request) {
        // 1. 内容去首尾空格后校验长度（上界 500 由 ReviewRequest 的 @Size 兜住）
        String content = request.getContent() == null ? null : request.getContent().strip();
        if (content == null || content.length() < 2) {
            return Result.error(400, CONTENT_INVALID_MESSAGE);
        }

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        // 2. 事务内完成数据库操作
        Result<Map<String, Long>> result = transactionTemplate.execute(status -> {
            // 2.1 锁定菜品行，防止并发评价时统计互相覆盖
            Dish dish = lockDish(dishId);
            if (dish == null || !Integer.valueOf(1).equals(dish.getStatus())) {
                return Result.error(404, DISH_NOT_FOUND_MESSAGE);
            }

            // 2.2 应用层查重，给出友好提示
            if (countReviewed(dishId, userId) > 0) {
                return Result.error(409, DUPLICATE_REVIEW_MESSAGE);
            }

            // 2.3 插入评价，并发重复由 uk_dish_user 唯一索引兜底
            review re = new review();
            re.setDishId(dishId);
            re.setUserId(userId);
            re.setRating(request.getRating());
            re.setContent(content);
            try {
                reviewMapper.insert(re);
            } catch (DuplicateKeyException exception) {
                return Result.error(409, DUPLICATE_REVIEW_MESSAGE);
            }

            // 2.4 重算菜品冗余统计（平均分 + 评价数量）
            dishMapper.recomputeReviewStats(dishId);

            return new Result<>(0, "评价成功", Map.of("reviewId", re.getId()));
        });

        // 3. 事务提交成功后才操作 Redis；Redis 失败不回滚已提交的数据库事务
        if (result != null && result.getCode() == 0) {
            syncRedisAfterCommit(dishId);
        }
        return result;
    }

    private Dish lockDish(Long dishId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(Dish::getId).eq(dishId)
                .forUpdate();
        return dishMapper.selectOneByQueryAs(wrapper, Dish.class);
    }

    private long countReviewed(Long dishId, Long userId) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(review::getDishId).eq(dishId)
                .and(review::getUserId).eq(userId);
        return reviewMapper.selectCountByQuery(wrapper);
    }

    /**
     * 删除菜品详情缓存（让下次查询回源数据库），然后同步热门榜。
     * 两个动作各自兜住异常：详情缓存可以靠 TTL 兜底，榜单可以靠定时校准兜底。
     */
    private void syncRedisAfterCommit(Long dishId) {
        try {
            stringRedisTemplate.delete(DISH_DETAIL_CACHE_PREFIX + dishId);
        } catch (Exception exception) {
            log.warn("菜品详情缓存删除失败, dishId={}, 由缓存TTL兜底", dishId, exception);
        }
        syncHotDishes(dishId);
    }

    private void syncHotDishes(Long dishId) {
        try {
            // 重新查库，拿到刚重算过的 avg_rating / review_count
            Dish dish = dishMapper.selectOneById(dishId);
            if (dish == null || !Integer.valueOf(1).equals(dish.getStatus())) {
                return;
            }
            // 榜单 key 不存在时先按数据库全量初始化，避免只写入一个菜品形成残缺榜单
            if (!stringRedisTemplate.hasKey(HOT_DISHES_KEY)) {
                initHotDishes();
            }
            // ZADD 覆盖写入，不使用 ZINCRBY
            stringRedisTemplate.opsForZSet().add(
                    HOT_DISHES_KEY, String.valueOf(dishId), hotScore(dish));
        } catch (Exception exception) {
            log.error("热门榜同步失败, dishId={}, 需依赖定时全量校准恢复", dishId, exception);
        }
    }

    private void initHotDishes() {
        QueryWrapper wrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);
        List<Dish> dishes = dishMapper.selectListByQueryAs(wrapper, Dish.class);
        for (Dish dish : dishes) {
            stringRedisTemplate.opsForZSet().add(
                    HOT_DISHES_KEY, String.valueOf(dish.getId()), hotScore(dish));
        }
    }

    /**
     * 热度分 = 评价数 × 100 + round(平均分 × 10)
     * 用 BigDecimal 保证 4.7 × 10 精确等于 47，而不是 46.999...
     */
    private double hotScore(Dish dish) {
        BigDecimal avgRating = dish.getAvgRating() == null ? BigDecimal.ZERO : dish.getAvgRating();
        long reviewCount = dish.getReviewCount() == null ? 0L : dish.getReviewCount();
        return reviewCount * 100 + avgRating.multiply(BigDecimal.TEN)
                .setScale(0, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
