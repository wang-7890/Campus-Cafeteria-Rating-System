package com.spring.restaurantdishes.model.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AiSummary {
    private Long dishId;
    private String summary;
    private List<String> pros;
    private List<String> cons;
    private Long reviewCount;
    private String generatedAt;
}
