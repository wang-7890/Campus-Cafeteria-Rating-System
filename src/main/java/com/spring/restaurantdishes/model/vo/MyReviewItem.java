package com.spring.restaurantdishes.model.vo;

import lombok.Data;

@Data
public class MyReviewItem {
    private Long id;
    private Long dishId;
    private Long userId;
    private String nickname;
    private Integer rating;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String dishName;//菜品名称
    private String canteenName;//餐厅名字
}
