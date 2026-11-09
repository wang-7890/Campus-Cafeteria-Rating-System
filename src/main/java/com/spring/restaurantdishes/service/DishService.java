package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.DishListItem;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.dataBasePojo.review;

import java.util.List;

public interface DishService {
    PageBean<DishListItem> pageDishes(DishPage dishPage);

    List<DishListItem> Hotdishes(Number limit);

    Result DishDetail(Number id);

    Result getDishReviews(DishDetailPage dishDetailPage, Number id);
}
