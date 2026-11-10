package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.RequestDetails.ReviewRequest;
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

    /**
     * 发表评价（接口文档 5.1）
     * 登录由 loginInterceptor 保证；userId 取当前登录用户，不信任前端传入。
     */
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
}
