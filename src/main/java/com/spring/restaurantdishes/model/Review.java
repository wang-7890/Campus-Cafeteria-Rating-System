package com.spring.restaurantdishes.model;

import lombok.Data;


//评价信息对象
@Data
public class Review {
    private Number id;//评价id
    private Number dishId;//菜品id
    private Number userId;//评价用户id
    private String nickname;//评价用户昵称
    private Number rating;//评分
    private String content;//评价内容
    private String createdAt;//创建时间
    private String updatedAt;//更新时间
}
