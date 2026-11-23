package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.AdminAddDish;
import com.spring.restaurantdishes.model.RequestDetails.AdminGetDishes;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.DataBase.Dish;

import java.util.Map;

public interface AdminService {
    Result<PageBean<Dish>> getDishesDetail(AdminGetDishes adminGetDishes);

    Result<Map<String, Long>> addNew(AdminAddDish newDish);

    Result<Void> updateDish(Long id, AdminAddDish newDish);

    Result<Void> deleteDish(Long id);
}
