package com.spring.restaurantdishes.model.dataBasePojo;

import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("review")
public class review {
    private Number id;
    private Number dishId;
    private Number userId;
    private Number rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
