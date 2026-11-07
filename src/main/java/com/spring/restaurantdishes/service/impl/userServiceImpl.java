package com.spring.restaurantdishes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.userMapper;
import com.spring.restaurantdishes.config.BCryptConfig;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.userInfo;
import com.spring.restaurantdishes.service.userService;
import com.spring.restaurantdishes.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class userServiceImpl implements userService {
    @Autowired
    private userMapper userMapper;
    @Autowired
    private BCryptConfig bCryptConfig;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public Result register(String username,String password,String nickname) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(userInfo::getUsername).eq(username);
        userInfo userInfo = userMapper.selectOneByQuery(queryWrapper);
        if(userInfo!=null){
            return Result.error(409, "用户名已存在");
        }
        else {
            String pwd = bCryptConfig.passwordEncoder().encode(password);
            userInfo newUser = new userInfo();
            newUser.setUsername(username.strip());
            newUser.setPassword(pwd);
            newUser.setRole("USER");
            newUser.setNickname(nickname);
            newUser.setStatus(1);
            userMapper.insert(newUser);
            return Result.success("注册成功");
        }
    }

    @Override
    public Map<String, Object> login(userInfo user) {
        String jti = UUID.randomUUID().toString();
        String token = JwtUtil.genToken(user.getId(),user.getRole(),jti);
//        QueryWrapper queryWrapper = QueryWrapper.create().where(userInfo::getUsername).eq(username);
//        userInfo user = userMapper.selectOneByQuery(queryWrapper);
//        Map<String,Object> userMsg = new HashMap<>();
//        Map<String,Object> usermap = new HashMap<>();
//        userMsg.put("id",user.getId());
//        userMsg.put("username",user.getUsername());
//        userMsg.put("role",user.getRole());
//        userMsg.put("iat", LocalDateTime.now());
//        userMsg.put("exp",LocalDateTime.now().plusHours(2));
//        String tokens = JwtUtil.genToken(userMsg);
//        userMsg.put("token",tokens);
//        userMsg.put("tokenType","bearer");
//        userMsg.put("expiresIn",7200);
//        try {
//            userMsg.put("userInfo",objectMapper.writeValueAsString(user));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        Map<String,Object> session = new HashMap<>();
        session.put("userId",user.getId());
        session.put("role",user.getRole());
        try{
            redisTemplate.opsForValue().set("canteen:session:" + jti, objectMapper.writeValueAsString(session),
                    Duration.ofHours(2));
        } catch (JsonProcessingException exception){
            throw new IllegalStateException("登录会话保存失败", exception);
        }
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("nickname", user.getNickname());
        safeUser.put("role", user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");
        data.put("expiresIn", 7200);
        data.put("userInfo", safeUser);
        return data;
    }
}
