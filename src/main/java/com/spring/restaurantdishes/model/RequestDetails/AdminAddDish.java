package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminAddDish {
    @NotEmpty(message = "不能为空")
    @Min(1)
    @Max(50)
    private String name;
    @NotEmpty(message = "不能为空")
    @Min(1)
    @Max(30)
    private String canteenName;
    @NotEmpty(message = "不能为空")
    @Min(1)
    @Max(50)
    private String windowName;
    @NotEmpty(message = "不能为空")
    @Min(1)
    @Max(20)
    private String category;
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "价格不能小于0")
    @DecimalMax(value = "999999.99", inclusive = true, message = "价格不能大于 999999.99")
    @Digits(integer = 6, fraction = 2, message = "最多两位小数，且不超过 999999.99")
    private BigDecimal price;
    @NotBlank(message = "评价不能为空")
    @Max(500)
    private String description;
    @NotBlank(message = "图片不能为空")
    @Max(255)
    private String imgUrl;
    private Integer status = 1;
}
