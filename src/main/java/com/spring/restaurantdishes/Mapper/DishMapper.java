package com.spring.restaurantdishes.Mapper;

import com.mybatisflex.core.BaseMapper;
import com.spring.restaurantdishes.model.dataBasePojo.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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
}
