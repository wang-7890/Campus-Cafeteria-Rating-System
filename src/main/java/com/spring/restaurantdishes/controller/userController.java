package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.userInfo;
import com.spring.restaurantdishes.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
public class userController {
    @Autowired
    private userService userService;
    @PostMapping("/auth/register")
    public Result register(@RequestBody @Validated userInfo user){
        userService.register(user);
        return Result.success();
    }
}
