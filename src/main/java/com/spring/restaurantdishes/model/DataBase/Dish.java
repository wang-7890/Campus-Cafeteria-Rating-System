package com.spring.restaurantdishes.model.DataBase;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * dish表实体类对象
 */
@Data
@Table("dish")
public class Dish {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String name;
    private String canteenName;
    private String windowName;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    private Integer status;
    private LocalDateTime createdAt;
    private BigDecimal avgRating;
    private Long reviewCount;
    private String description;
    private LocalDateTime updatedAt;
}
