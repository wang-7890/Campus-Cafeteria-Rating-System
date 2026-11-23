package com.spring.restaurantdishes.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 推荐项（接口文档 2.7）：
 * 菜品事实字段一律由后端按 dishId 从数据库回填，不采用大模型返回的字段值。
 */
@Data
public class AiRecommendItem {
    private Long dishId;
    private String name;
    private String canteenName;
    private String windowName;
    private String category;
    private BigDecimal price;
    private BigDecimal avgRating;
    private Long reviewCount;
    private String reason;
}
