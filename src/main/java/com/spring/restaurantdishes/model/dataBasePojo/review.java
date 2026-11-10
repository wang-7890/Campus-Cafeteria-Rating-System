package com.spring.restaurantdishes.model.dataBasePojo;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("review")
public class review {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long dishId;
    private Long userId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
