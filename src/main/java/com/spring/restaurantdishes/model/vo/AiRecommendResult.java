package com.spring.restaurantdishes.model.vo;

import lombok.Data;

import java.util.List;

/**
 * AI 智能推荐结果（接口文档 2.7）
 */
@Data
public class AiRecommendResult {
    private String greeting;
    private List<AiRecommendItem> list;
}
