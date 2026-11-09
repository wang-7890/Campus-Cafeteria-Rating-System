package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.dataBasePojo.user;

import java.util.Map;

public interface userService {
    Result register(String username,String password,String nickname);

    Map<String, Object> login(user user);

    void logout();
}
