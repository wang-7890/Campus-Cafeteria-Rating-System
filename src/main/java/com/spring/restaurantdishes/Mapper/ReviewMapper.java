package com.spring.restaurantdishes.Mapper;

import com.mybatisflex.core.BaseMapper;
import com.spring.restaurantdishes.model.vo.MyReviewItem;
import com.spring.restaurantdishes.model.vo.ReviewItem;
import com.spring.restaurantdishes.model.DataBase.review;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ReviewMapper extends BaseMapper<review> {

    @Select("SELECT COUNT(*) FROM review r WHERE r.dish_id = #{dishId}")
    long countByDishId(@Param("dishId") Long dishId);

    @Select("SELECT r.id, r.dish_id AS dishId, r.user_id AS userId, u.nickname, r.rating, r.content, " +
            "DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, " +
            "DATE_FORMAT(r.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt " +
            "FROM review r JOIN `user` u ON u.id = r.user_id " +
            "WHERE r.dish_id = #{dishId} " +
            "ORDER BY r.created_at DESC, r.id DESC " +
            "LIMIT #{offset}, #{size}")
    List<ReviewItem> pageReviewItems(@Param("dishId") Long dishId, @Param("offset") int offset,
                                     @Param("size") int size);

    @Update("UPDATE review SET rating = #{rating}, content = #{content} WHERE id = #{reviewId}")
    int updateReview(@Param("reviewId") Long reviewId,
                     @Param("rating") Integer rating,
                     @Param("content") String content);
    @Select("SELECT COUNT(*) FROM review WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT r.id, r.dish_id AS dishId, r.user_id AS userId, usr.nickname, " +
            "d.name AS dishName, d.canteen_name AS canteenName, r.rating, r.content, " +
            "DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt, " +
            "DATE_FORMAT(r.updated_at, '%Y-%m-%d %H:%i:%s') AS updatedAt " +
            "FROM review r " +
            "JOIN user usr ON usr.id = r.user_id " +
            "JOIN dish d ON d.id = r.dish_id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_at DESC, r.id DESC " +
            "LIMIT #{offset}, #{size}")
    List<MyReviewItem> getAllMyReviews(@Param("userId") Long userId,
                                       @Param("offset") int offset,
                                       @Param("size") int size);
}
