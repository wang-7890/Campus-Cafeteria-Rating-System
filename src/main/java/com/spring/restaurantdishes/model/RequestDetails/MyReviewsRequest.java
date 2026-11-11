package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 查询我的评价分页参数（接口文档 5.4：page/size 为 Query 参数，均可省略）
 */
@Data
public class MyReviewsRequest {
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 50, message = "每页最多查询50条")
    private Integer size = 10;
}
