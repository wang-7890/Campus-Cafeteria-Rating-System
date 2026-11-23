package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.*;
import com.spring.restaurantdishes.model.vo.*;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.model.DataBase.Dish;
import com.spring.restaurantdishes.model.DataBase.review;

import java.util.List;

public interface DishService {
    PageBean<DishListItem> pageDishes(DishPage dishPage);

    List<DishListItem> Hotdishes(Number limit);

    Result DishDetail(Number id);

    Result<PageBean<ReviewItem>> getDishReviews(DishDetailPage dishDetailPage, Long id);

    void syncNewDishHotList(Dish dish);
}
