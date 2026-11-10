package com.spring.restaurantdishes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 分页对象，包含查出来的值和其他参数
 */
public class PageBean<T> {
    private List<T> list;
    private long page;
    private long size;
    private long total;
    private long pages;
}
