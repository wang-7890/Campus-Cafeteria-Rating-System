package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.userInfo;

import java.util.Map;

public interface userService {
    Result register(String username,String password,String nickname);

    Map<String, Object> login(userInfo user);
}
