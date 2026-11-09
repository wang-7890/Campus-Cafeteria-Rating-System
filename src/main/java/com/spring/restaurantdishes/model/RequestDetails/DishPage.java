package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分页请求对象
 */
@Data
public class DishPage {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = 1;

    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量必须大于等于1")
    @Max(value = 50, message = "每页最多查询50条")
    private Integer size = 10;

    @Size(max = 50, message = "搜索关键词最多50个字符")
    private String keyword;

    private String canteenName;

    private String category;

    @NotNull(message = "排序方式不能为空")
    @Pattern(
            regexp = "newest|rating|reviewCount",
            message = "排序方式只支持newest、rating、reviewCount"
    )
    private String sort = "newest";
}
