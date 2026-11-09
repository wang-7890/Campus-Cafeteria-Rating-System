package com.spring.restaurantdishes.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.DishMapper;
import com.spring.restaurantdishes.model.DishListItem;
import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.DishDetailPage;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.RequestDetails.DishPage;
import com.spring.restaurantdishes.model.dataBasePojo.Dish;
import com.spring.restaurantdishes.model.dataBasePojo.review;
import com.spring.restaurantdishes.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishMapper dishMapper;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/dishes")
    public ResponseEntity<Result<PageBean<DishListItem>>> dishes(
            @Validated @ModelAttribute DishPage dishPage) {
        return ResponseEntity.ok(Result.success(dishService.pageDishes(dishPage)));
    }

    @GetMapping("/dishes/hot")
    public ResponseEntity<Result<List<DishListItem>>> hotDishes(@RequestParam Number limit){
        return ResponseEntity.ok(Result.success(dishService.Hotdishes(limit)));
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<Result> dishesDetails(@RequestParam(required = true) Number id){
        return ResponseEntity.ok(dishService.DishDetail(id));
    }

    @GetMapping("/dishes/{id}/reviews")
    public Result dishReviews(@PathVariable Number id,
                              @Validated @ModelAttribute DishDetailPage dishDetailPage){
        return dishService.getDishReviews(dishDetailPage,id);
    }

}
