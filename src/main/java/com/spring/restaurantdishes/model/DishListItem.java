package com.spring.restaurantdishes.model;

import lombok.Data;

@Data
public class DishListItem {
    private Number id;
    private String name;
    private String canteenName;
    private String windowName;
    private String category;
    private String price;
    private String imageUrl;
    private String avgRating;
    private String reviewCount;
}
