package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.vo.MyReviewItem;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.MyReviewsRequest;
import com.spring.restaurantdishes.model.RequestDetails.ReviewRequest;
import com.spring.restaurantdishes.model.RequestDetails.UpdateReviewRequest;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.service.ReviewService;
import com.spring.restaurantdishes.utils.threadLocalUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/dishes/{id}/reviews")
    public ResponseEntity<Result<Map<String, Long>>> createReview(
            @PathVariable Long id,
            @Validated @RequestBody ReviewRequest request) {
        Map<String, Object> claims = threadLocalUtils.get();
        Long userId = (Long) claims.get("userId");

        Result<Map<String, Long>> result = reviewService.createReview(id, userId, request);
        if (result.getCode() == 0) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Result<Void>> updateReview(
            @PathVariable("id") Long reviewId,
            @Validated @RequestBody UpdateReviewRequest request) {
        Map<String, Object> claims = threadLocalUtils.get();
        Long userId = (Long) claims.get("userId");

        Result<Void> result = reviewService.update(userId, reviewId, request);
        if (result.getCode() == 0) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Result<Void>> deleteReview(@PathVariable("id") Long reviewId){
        Map<String,Object> claims = threadLocalUtils.get();
        Long userId = (Long) claims.get("userId");
        String role = (String) claims.get("role");
        Result<Void> result = reviewService.deleteComment(reviewId,userId,role);
        if(result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/users/me/reviews")
    public ResponseEntity<Result<PageBean<MyReviewItem>>> myReviews(
            @Validated @ModelAttribute MyReviewsRequest request){
        Map<String,Object> claims = threadLocalUtils.get();
        Long userId = (Long) claims.get("userId");
        Result<PageBean<MyReviewItem>> result = reviewService.getMyReviews(request,userId);
        if (result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);

    }
}
