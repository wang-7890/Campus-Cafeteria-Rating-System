package com.spring.restaurantdishes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Table("user")
public class userInfo {
    private Number id; //用户ID
    private String username; //登录用户名
    private String nickname; //用户昵称
    private String role;//用户角色
    @JsonIgnore
    @ToString.Exclude
    private String password;
    private Integer status;
}
