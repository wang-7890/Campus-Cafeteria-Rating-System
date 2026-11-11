package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.*;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DishController {
    @Autowired
    private DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    //分页查询所有菜品信息
    @GetMapping("/dishes")
    public ResponseEntity<Result<PageBean<DishListItem>>> dishes(
            @Validated @ModelAttribute DishPage dishPage) {
        return ResponseEntity.ok(Result.success(dishService.pageDishes(dishPage)));
    }

    //查询前10热门的菜品（接口文档 4.2：limit 默认 10，范围 1～20）
    @GetMapping("/dishes/hot")
    public ResponseEntity<Result<List<DishListItem>>> hotDishes(
            @RequestParam(defaultValue = "10") Integer limit) {
        if (limit == null || limit < 1 || limit > 20) {
            return ResponseEntity.status(400)
                    .body(Result.error(400, "limit: 返回数量必须在1～20之间"));
        }
        return ResponseEntity.ok(Result.success(dishService.Hotdishes(limit)));
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<Result<DishDetail>> dishesDetails(@PathVariable Long id) {
        Result<DishDetail> result = dishService.DishDetail(id);
        if (result.getCode() == 0) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/dishes/{id}/reviews")
    public ResponseEntity<Result<PageBean<ReviewItem>>> dishReviews(@PathVariable Long id,
                                                                    @Validated @ModelAttribute DishDetailPage dishDetailPage) {
        Result<PageBean<ReviewItem>> result = dishService.getDishReviews(dishDetailPage, id);
        if (result.getCode() == 0) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(result.getCode()).body(result);
    }

}
