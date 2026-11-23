package com.spring.restaurantdishes.model.ai;

import lombok.Data;

import java.util.List;

/**
 * AI 推荐的模型原始输出（AI 边界对象）：
 * 大模型只许产出 greeting 和"菜品ID＋推荐理由"，其余字段由后端回填。
 */
@Data
public class AiRecommendRaw {
    private String greeting;
    private List<Item> list;

    @Data
    public static class Item {
        private Long dishId;
        private String reason;
    }
}
