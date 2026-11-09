package com.spring.restaurantdishes.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDetail {
    private Long id;
    private String name;
    private String canteenName;
    private String windowName;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    private BigDecimal avgRating;
    private Long reviewCount;
    private String description;
}
