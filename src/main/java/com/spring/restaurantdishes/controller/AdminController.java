package com.spring.restaurantdishes.controller;

import com.spring.restaurantdishes.model.PageBean;
import com.spring.restaurantdishes.model.RequestDetails.AdminAddDish;
import com.spring.restaurantdishes.model.RequestDetails.AdminGetDishes;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.DataBase.Dish;
import com.spring.restaurantdishes.service.AdminService;
import com.spring.restaurantdishes.utils.threadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController("/api")
public class AdminController {

    private final Map<String ,Object> claims =threadLocalUtils.get();

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/dishes")
    public ResponseEntity<Result<PageBean<Dish>>> adminGetDishes(@ModelAttribute @Validated AdminGetDishes adminGetDishes){
        String role = (String) claims.get("role");
        if(role.equals("USER")){
            return ResponseEntity.status(403).body(Result.error(403,"普通用户无权访问"));
        }
        Result<PageBean<Dish>> result = adminService.getDishesDetail(adminGetDishes);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/admin/dishes")
    public ResponseEntity<Result<Map<String,Long>>> addNewDish(@RequestBody @Validated AdminAddDish newDish) {
        String role = (String) claims.get("role");
        if (role.equals("USER")) {
            return ResponseEntity.status(403).body(Result.error(403, "普通用户无权访问"));
        }
        Result<Map<String, Long>> result = adminService.addNew(newDish);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/admin/dishes/{id}")
    public ResponseEntity<Result<Void>> adminUpdateDish(@PathVariable Long id,@RequestBody @Validated AdminAddDish newDish){
        String role = (String) claims.get("role");
        if (role.equals("USER")) {
            return ResponseEntity.status(403).body(Result.error(403, "普通用户无权访问"));
        }
        Result<Void> result = adminService.updateDish(id,newDish);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/admin/dishes/{id}")
    public ResponseEntity<Result<Void>> adminDeleteDish(@PathVariable Long id){
        String role = (String) claims.get("role");
        if (role.equals("USER")) {
            return ResponseEntity.status(403).body(Result.error(403, "普通用户无权访问"));
        }
        Result<Void> result = adminService.deleteDish(id);
        return ResponseEntity.ok(result);
    }
}
