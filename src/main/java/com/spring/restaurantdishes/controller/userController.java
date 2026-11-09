package com.spring.restaurantdishes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.userMapper;
import com.spring.restaurantdishes.config.BCryptConfig;
import com.spring.restaurantdishes.model.RequestDetails.LoginRequest;
import com.spring.restaurantdishes.model.RequestDetails.RegisterRequest;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.dataBasePojo.user;
import com.spring.restaurantdishes.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class userController {
    @Autowired
    private userService userService;
    @Autowired
    private userMapper usermapper;
    @Autowired
    private BCryptConfig bCryptConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @PostMapping("/auth/register")
    public ResponseEntity<Result<Void>> register(@Validated @RequestBody RegisterRequest request){
        Result<Void> result = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getNickname());
        if (result.getCode() == 0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Result<?>> login(@Validated @RequestBody LoginRequest request){
        QueryWrapper queryWrapper = QueryWrapper.create().where(user::getUsername).eq(request.getUsername());
        user account = usermapper.selectOneByQuery(queryWrapper);
        if(account==null || !bCryptConfig.matches(request.getPassword(),account.getPassword())){
            return ResponseEntity.status(400).body(Result.error(400,"用户名或密码错误"));
        }
        if(account.getStatus() !=null && account.getStatus() == 0){
            return ResponseEntity.status(403).body(Result.error(403,"账号已被禁用"));
        }
        return ResponseEntity.ok(Result.success(userService.login(account)));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Result<Void>> logout(){
        userService.logout();
        return ResponseEntity.ok(Result.successMessage("退出成功"));
    }
}
