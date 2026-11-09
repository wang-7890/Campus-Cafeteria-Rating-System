package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DishDetailPage {
    @Min(1)
    private Number page = 1;
    @Min(10)
    @Max(50)
    private Number size = 10;
}
