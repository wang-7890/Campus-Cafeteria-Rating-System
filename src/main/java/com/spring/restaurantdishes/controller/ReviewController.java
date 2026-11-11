package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.MyReviewItem;
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

import java.util.List;
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

    /**
     * 修改评价（接口文档 5.2）
     * 登录由 loginInterceptor 保证；userId 取当前登录用户，不信任前端传入。
     */
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

    /**
     * 查询我的评价（接口文档 5.4）
     * 登录由 loginInterceptor 保证；userId 取当前登录用户，不信任前端传入。
     * page/size 为 Query 参数，与 DishController 的分页接口保持一致的绑定方式。
     */
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
