package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI 辅助写评价请求体（接口文档 7.3）
 */
@Data
public class AiDraftRequest {

    @NotNull(message = "菜品ID不能为空")
    @Min(value = 1, message = "菜品ID必须为正整数")
    private Long dishId;

    @NotBlank(message = "关键词不能为空")
    @Size(min = 2, max = 100, message = "关键词需为2～100个字符")
    private String keywords;

    @Min(value = 1, message = "意向评分必须为1～5的整数")
    @Max(value = 5, message = "意向评分必须为1～5的整数")
    private Integer rating;

    @Size(max = 20, message = "文风最多20个字符")
    private String style = "自然";
}
