package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发表评价请求体（接口文档 5.1）
 */
@Data
public class ReviewRequest {
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须为1～5的整数")
    @Max(value = 5, message = "评分必须为1～5的整数")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Size(max = 500, message = "评价内容最多500个字符")
    private String content;
}
