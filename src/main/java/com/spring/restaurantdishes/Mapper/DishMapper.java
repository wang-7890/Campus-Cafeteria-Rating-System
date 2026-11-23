package com.spring.restaurantdishes.Mapper;

import com.mybatisflex.core.BaseMapper;
import com.spring.restaurantdishes.model.vo.DishListItem;
import com.spring.restaurantdishes.model.DataBase.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 重算菜品冗余统计（数据库设计文档 5.1）：
     * 无评价时 COALESCE 归 0，删除最后一条评价后统计正确回 0。
     */
    @Update("UPDATE dish d " +
            "SET d.avg_rating = COALESCE((SELECT ROUND(AVG(r.rating), 1) FROM review r WHERE r.dish_id = #{dishId}), 0.0), " +
            "    d.review_count = (SELECT COUNT(*) FROM review r WHERE r.dish_id = #{dishId}) " +
            "WHERE d.id = #{dishId}")
    int recomputeReviewStats(@Param("dishId") Long dishId);

    @Select("SELECT d.id, d.name, d.canteen_name AS canteenName, d.window_name AS windowName, " +
            "d.category, d.price, d.image_url AS imageUrl, " +
            "d.avg_rating AS avgRating, d.review_count AS reviewCount " +
            "FROM dish d " +
            "JOIN review r ON r.dish_id = d.id " +
            "WHERE r.user_id = #{userId} AND d.status = 1 " +
            "ORDER BY r.created_at DESC " +
            "LIMIT 10")
    List<DishListItem> getReviewedDishes(@Param("userId") Long userId);
}
