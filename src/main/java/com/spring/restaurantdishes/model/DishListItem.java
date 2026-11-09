package com.spring.restaurantdishes.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * dish类公用属性类，分页查询查出来的属性
 */
@Data
public class DishListItem {
    private Long id;
    private String name;
    private String canteenName;
    private String windowName;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    private BigDecimal avgRating;
    private Long reviewCount;
}
