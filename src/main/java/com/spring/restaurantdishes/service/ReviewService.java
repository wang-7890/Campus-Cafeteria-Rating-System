package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.RequestDetails.ReviewRequest;
import com.spring.restaurantdishes.model.Result;

import java.util.Map;

public interface ReviewService {
    Result<Map<String,Long>> createReview(Long dishId, Long userId, ReviewRequest request);
}
