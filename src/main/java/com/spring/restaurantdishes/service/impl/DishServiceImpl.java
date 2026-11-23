package com.spring.restaurantdishes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.DishMapper;
import com.spring.restaurantdishes.Mapper.ReviewMapper;
import com.spring.restaurantdishes.model.*;
import com.spring.restaurantdishes.model.vo.*;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.DataBase.Dish;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class DishServiceImpl implements DishService {

    private static final String HOT_DISHES_KEY = "canteen:hot:dishes";

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageBean<DishListItem> pageDishes(DishPage dishPage) {
        Integer pageNum = dishPage.getPage();
        Integer pageSize = dishPage.getSize();

        QueryWrapper wrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);

        if(StringUtils.hasText(dishPage.getKeyword())){
            wrapper.and(Dish::getName).like(dishPage.getKeyword().trim());
        }
        if (StringUtils.hasText(dishPage.getCanteenName())){
            wrapper.and(Dish::getCanteenName).eq(dishPage.getCanteenName().trim());
        }
        if (StringUtils.hasText(dishPage.getCategory())){
            wrapper.and(Dish::getCategory).eq(dishPage.getCategory().trim());
        }

        String sort = dishPage.getSort();
        switch (sort) {
            case "rating":
                wrapper.orderBy(Dish::getAvgRating, false)
                        .orderBy(Dish::getReviewCount, false)
                        .orderBy(Dish::getId, false);
                break;
            case "reviewCount":
                wrapper.orderBy(Dish::getReviewCount,false)
                        .orderBy(Dish::getAvgRating,false)
                        .orderBy(Dish::getId,false);
                break;
            case "newest":
            default:
                wrapper.orderBy(Dish::getCreatedAt,false)
                        .orderBy(Dish::getId,false);
                break;
        }

        Page<DishListItem> flexPage = dishMapper.paginateAs(pageNum,pageSize,wrapper, DishListItem.class);

        PageBean<DishListItem> result = new PageBean<>();

        result.setList(flexPage.getRecords());
        result.setPage(flexPage.getPageNumber());
        result.setSize(flexPage.getPageSize());
        result.setTotal(flexPage.getTotalRow());
        result.setPages(flexPage.getTotalPage());

        return result;
    }

    @Override
    public List<DishListItem> Hotdishes(Number limit) {
        int size = limit == null ? 10 : limit.intValue();
        QueryWrapper queryWrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);
        queryWrapper.orderBy(Dish::getReviewCount,false)
                .orderBy(Dish::getAvgRating,false)
                .orderBy(Dish::getId,false)
                .limit(size);
        List<DishListItem> dishListItems = dishMapper.selectListByQueryAs(queryWrapper, DishListItem.class);

        // 榜单 key 不存在时按 MySQL 全量懒重建热门榜 ZSET（数据库文档 6.3）；
        // Redis 异常（含 hasKey 返回 null）不影响本次返回，直接使用 MySQL 查询结果
        try {
            if (!stringRedisTemplate.hasKey(HOT_DISHES_KEY)) {
                rebuildHotList();
            }
        } catch (Exception exception) {
            log.warn("热门榜Redis操作失败, 本次直接返回MySQL查询结果", exception);
        }
        return dishListItems;
    }
    @Override
    public void syncNewDishHotList(Dish dish){
        try{
            if (stringRedisTemplate.hasKey(HOT_DISHES_KEY)){
                stringRedisTemplate.opsForZSet().add(
                        HOT_DISHES_KEY,String.valueOf(dish.getId()),
                        hotScore(dish)
                );
            }else {
                rebuildHotList();
            }
        }catch (Exception exception){
            log.warn("新增菜品热门榜同步失败, dishId={}", dish.getId(), exception);
        }
    }

    private void rebuildHotList() {
        try {
            QueryWrapper wrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);
            List<Dish> dishes = dishMapper.selectListByQueryAs(wrapper, Dish.class);
            Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
            for (Dish dish : dishes) {
                tuples.add(ZSetOperations.TypedTuple.of(
                        String.valueOf(dish.getId()), hotScore(dish)));
            }
            stringRedisTemplate.opsForZSet().add(HOT_DISHES_KEY, tuples);
        } catch (Exception exception) {
            log.warn("热门榜懒重建失败, 本次直接返回MySQL查询结果", exception);
        }
    }

    /**
     * 热度分 = 评价数 × 100 + round(平均分 × 10)（数据库文档 6.3）
     */
    private double hotScore(Dish dish) {
        BigDecimal avgRating = dish.getAvgRating() == null ? BigDecimal.ZERO : dish.getAvgRating();
        long reviewCount = dish.getReviewCount() == null ? 0L : dish.getReviewCount();
        return reviewCount * 100 + avgRating.multiply(BigDecimal.TEN)
                .setScale(0, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public Result DishDetail(Number id) {
        try {
            String cacheJson = stringRedisTemplate.opsForValue().get("canteen:dish:detail:"+id);
            if(cacheJson!=null){
                DishDetail cached = objectMapper.readValue(cacheJson, DishDetail.class);
                if(cached!=null){
                    return Result.success(cached);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("菜品详情缓存读取失败，dishId ={},回源数据库",id,e);
        }

        QueryWrapper wrapper = QueryWrapper.create().where(Dish::getId).eq(id).and(Dish::getStatus).eq(1);
        DishDetail dishDetail = dishMapper.selectOneByQueryAs(wrapper, DishDetail.class);
        if (dishDetail == null) {
            return Result.error(404, "菜品不存在或已下架");
        }
        try {
            stringRedisTemplate.opsForValue().set("canteen:dish:detail:" + id,objectMapper.writeValueAsString(dishDetail),Duration.ofSeconds(1800));
        } catch (JsonProcessingException e) {
            log.warn("菜品详情缓存读取失败，dishId ={},回源数据库",id,e);
        }
        return Result.success(dishDetail);
    }

    @Override
    public Result<PageBean<ReviewItem>> getDishReviews(DishDetailPage dishDetailPage, Long id) {
        Integer pageNum = dishDetailPage.getPage();
        Integer pageSize = dishDetailPage.getSize();
        //查询条件
        QueryWrapper queryWrapper = QueryWrapper.create().where(Dish::getId).eq(id);
        Dish dish = dishMapper.selectOneByQueryAs(queryWrapper, Dish.class);
        if(dish == null || dish.getStatus() == 0){
            return Result.error(404,"菜品不存在或已下架");
        }
        Long total = reviewMapper.countByDishId(id);

        List<ReviewItem> items = reviewMapper.pageReviewItems(id,(pageNum-1)*pageSize,pageSize);
        PageBean<ReviewItem> bean = new PageBean<>();
        bean.setList(items);
        bean.setPage(pageNum);
        bean.setSize(pageSize);
        bean.setTotal(total);
        bean.setPages(total == 0 ? 0 : (int)((total + pageSize - 1)/pageSize));
        return Result.success(bean);




    }


}
