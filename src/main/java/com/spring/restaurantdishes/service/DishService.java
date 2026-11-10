package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.*;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.model.dataBasePojo.review;

import java.util.List;

public interface DishService {
    PageBean<DishListItem> pageDishes(DishPage dishPage);

    List<DishListItem> Hotdishes(Number limit);

    Result DishDetail(Number id);

    Result<PageBean<ReviewItem>> getDishReviews(DishDetailPage dishDetailPage, Long id);
}
