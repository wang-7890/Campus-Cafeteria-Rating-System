package com.spring.restaurantdishes.model.ai;

import lombok.Data;

import java.util.List;

@Data
public class AiSummaryRaw {
    private String summary;
    private List<String> pros;
    private List<String> cons;
}
