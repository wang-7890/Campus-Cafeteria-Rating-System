package com.spring.restaurantdishes.model;

import lombok.Data;

@Data
public class userInfo {
    private Number id; //用户ID
    private String username; //登录用户名
    private String nickname; //用户昵称
    private String role; //用户角色
}
