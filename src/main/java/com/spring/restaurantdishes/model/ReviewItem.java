package com.spring.restaurantdishes.model;

import lombok.Data;

@Data
public class ReviewItem {
    private Long id;
    private Long dishId;
    private Long userId;
    private String nickname;
    private Integer rating;
    private String content;
    private String createdAt;
    private String updatedAt;

}
