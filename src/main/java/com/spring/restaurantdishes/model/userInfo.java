package com.spring.restaurantdishes.model;

import lombok.Data;

/**
 * 接口返回的公开用户信息。
 */
@Data
public class userInfo {
    private Number id;
    private String username;
    private String nickname;
    private String role;
}
