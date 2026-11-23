package com.spring.restaurantdishes.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.DishMapper;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.AdminAddDish;
import com.spring.restaurantdishes.model.RequestDetails.AdminGetDishes;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.DataBase.Dish;
import com.spring.restaurantdishes.service.AdminService;
import com.spring.restaurantdishes.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result<PageBean<Dish>> getDishesDetail(AdminGetDishes adminGetDishes) {
        int pageNum = adminGetDishes.getPage();
        int pageSize = adminGetDishes.getSize();

        QueryWrapper wrapper = QueryWrapper.create();
        switch (adminGetDishes.getStatus()){
            case 1:
                wrapper.and(Dish::getStatus).eq(1);
                break;
            case 0:
                wrapper.and(Dish::getStatus).eq(0);
                break;
            default:
                break;
        }
        if (StringUtils.hasText(adminGetDishes.getKeyword())){
            wrapper.and(Dish::getName).like(adminGetDishes.getKeyword());
        }
        if(StringUtils.hasText(adminGetDishes.getCanteenName())){
            wrapper.and(Dish::getCanteenName).eq(adminGetDishes.getCanteenName());
        }
        if(StringUtils.hasText(adminGetDishes.getCategory())){
            wrapper.and(Dish::getCategory).eq(adminGetDishes.getCategory());
        }
        wrapper.orderBy(Dish::getCreatedAt,false).orderBy(Dish::getUpdatedAt,false);
        Page<Dish> dishes = dishMapper.paginate(pageNum,pageSize,wrapper);

        PageBean<Dish> dishPageBean = new PageBean<>();
        dishPageBean.setList(dishes.getRecords());
        dishPageBean.setPage(dishes.getPageNumber());
        dishPageBean.setPages(dishes.getTotalPage());
        dishPageBean.setTotal(dishes.getTotalRow());
        dishPageBean.setSize(dishes.getPageSize());
        return Result.success(dishPageBean);
    }

    @Override
    public Result<Map<String, Long>> addNew(AdminAddDish newDish) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Dish dish = new Dish();
        Result<Map<String, Long>> result = transactionTemplate.execute(status -> {
            dish.setAvgRating(BigDecimal.valueOf(0));
            dish.setStatus(newDish.getStatus());
            dish.setCategory(newDish.getCategory());
            dish.setReviewCount(0L);
            dish.setImageUrl(newDish.getImgUrl());
            dish.setCanteenName(newDish.getCanteenName());
            dish.setName(newDish.getName());
            dish.setPrice(newDish.getPrice());
            dish.setDescription(newDish.getDescription());
            dish.setWindowName(newDish.getWindowName());

            dishMapper.insert(dish);
            return Result.success(Map.of("dishId", dish.getId()));
        });
        if (dish.getStatus() == 1) {
            dishService.syncNewDishHotList(dish);
        }
        return result;
    }

    @Override
    public Result<Void> updateDish(Long id, AdminAddDish newDish) {
        if(dishMapper.selectOneById(id) == null){
            return Result.error(404,"菜品不存在");
        }
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Result<Void> result = transactionTemplate.execute(status -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setName(newDish.getName());
            dish.setCanteenName(newDish.getCanteenName());
            dish.setWindowName(newDish.getWindowName());
            dish.setCategory(newDish.getCategory());
            dish.setPrice(newDish.getPrice());
            dish.setDescription(newDish.getDescription());
            dish.setImageUrl(newDish.getImgUrl());
            dish.setStatus(newDish.getStatus());
            dishMapper.update(dish);
            return Result.successMessage("修改成功");
        });

        try{
            stringRedisTemplate.delete("canteen:dish:detail:" + id);
        }catch (Exception exception){
            log.warn("菜品详情缓存删除失败, dishId={}, 由缓存TTL兜底", id, exception);
        }
        syncHotDishList(dishMapper.selectOneById(id));
        return result;
    }

    @Override
    public Result<Void> deleteDish(Long id) {
        Dish dish = dishMapper.selectOneById(id);
        if(dish == null){
            return Result.error(404,"菜品不存在");
        }
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Result<Void> result = transactionTemplate.execute(status -> {
            Dish update = new Dish();
            update.setStatus(0);
            QueryWrapper wrapper = QueryWrapper.create().where(Dish::getId).eq(id).and(Dish::getStatus).eq(1);
            int rows = dishMapper.updateByQuery(update,wrapper);
            if (rows == 0){
                return Result.error(409,"菜品已处于下架状态");
            }
            return Result.successMessage("下架成功");
        });
        if (result != null && result.getCode() == 0) {      // 修 P2：失败不做 Redis 副作用
            try {
                stringRedisTemplate.delete("canteen:dish:detail:" + id);
            } catch (Exception e) {
                log.warn("菜品详情缓存删除失败, dishId={}, 由缓存TTL兜底", id, e);
            }
            dish.setStatus(0);                              // 修 P0-2：让同步走 ZREM
            syncHotDishList(dish);
        }
        return result;
    }

    private void syncHotDishList(Dish dish){
        try {
            if(dish.getStatus() == 1){
                if(stringRedisTemplate.hasKey("canteen:hot:dishes")){
                    stringRedisTemplate.opsForZSet().add("canteen:hot:dishes",String.valueOf(dish.getId()),hotScore(dish));
                }else {
                    rebuildHotDishList();
                }
            }else {
                stringRedisTemplate.opsForZSet().remove("canteen:hot:dishes",String.valueOf(dish.getId()));
            }
        } catch (Exception e) {
            log.warn("热门榜同步失败, dishId={}, 依赖后续全量校准恢复", dish.getId(), e);
        }
    }

    private void rebuildHotDishList() {
        try {
            QueryWrapper queryWrapper = QueryWrapper.create().where(Dish::getStatus).eq(1);
            List<Dish> dishList = dishMapper.selectListByQueryAs(queryWrapper, Dish.class);
            Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
            for (Dish dish : dishList){
                tuples.add(ZSetOperations.TypedTuple.of(String.valueOf(dish.getId()),hotScore(dish)));
            }
            stringRedisTemplate.opsForZSet().add("canteen:hot:dishes",tuples);
        } catch (Exception exception) {
            log.warn("热门榜懒重建失败, 本次直接返回MySQL查询结果", exception);
        }
    }

    private double hotScore(Dish dish) {
        BigDecimal avgRating = dish.getAvgRating() == null ? BigDecimal.ZERO : dish.getAvgRating();
        long reviewCount = dish.getReviewCount() == null ? 0L : dish.getReviewCount();
        return reviewCount * 100 + avgRating.multiply(BigDecimal.TEN)
                .setScale(0, RoundingMode.HALF_UP).doubleValue();
    }


}
