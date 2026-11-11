package com.spring.restaurantdishes.service;

import com.spring.restaurantdishes.model.MyReviewItem;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.MyReviewsRequest;
import com.spring.restaurantdishes.model.RequestDetails.ReviewRequest;
import com.spring.restaurantdishes.model.RequestDetails.UpdateReviewRequest;
import com.spring.restaurantdishes.model.Result;

import java.util.Map;

public interface ReviewService {
    Result<Map<String, Long>> createReview(Long dishId, Long userId, ReviewRequest request);

    Result<Void> update(Long userId, Long reviewId, UpdateReviewRequest request);

    Result<Void> deleteComment(Long reviewId, Long userId, String role);

    Result<PageBean<MyReviewItem>> getMyReviews(MyReviewsRequest request, Long userId);
}
