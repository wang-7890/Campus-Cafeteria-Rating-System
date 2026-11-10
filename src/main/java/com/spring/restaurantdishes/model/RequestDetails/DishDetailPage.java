package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DishDetailPage {
    @Min(1)
    private Integer page = 1;
    @Min(1)
    @Max(50)
    private Integer size = 10;
}
