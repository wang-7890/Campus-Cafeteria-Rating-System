package com.spring.restaurantdishes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.userMapper;
import com.spring.restaurantdishes.config.BCryptConfig;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.DataBase.user;
import com.spring.restaurantdishes.model.vo.UserInfo;
import com.spring.restaurantdishes.service.userService;
import com.spring.restaurantdishes.utils.JwtUtil;
import com.spring.restaurantdishes.utils.threadLocalUtils;
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
        QueryWrapper queryWrapper = QueryWrapper.create().where(user::getUsername).eq(username);
        user existingUser = userMapper.selectOneByQuery(queryWrapper);
        if(existingUser!=null){
            return Result.error(409, "用户名已存在");
        }
        else {
            String pwd = bCryptConfig.passwordEncoder().encode(password);
            user newUser = new user();
            newUser.setUsername(username.strip());
            newUser.setPassword(pwd);
            newUser.setRole("USER");
            newUser.setNickname(nickname);
            newUser.setStatus(1);
            userMapper.insert(newUser);
            return Result.successMessage("注册成功");
        }
    }

    @Override
    public Map<String, Object> login(user user) {
        String jti = UUID.randomUUID().toString();
        String token = JwtUtil.genToken(user.getId(),user.getRole(),jti);
        Map<String,Object> session = new HashMap<>();
        session.put("userId",user.getId());
        session.put("role",user.getRole());
        try{
            redisTemplate.opsForValue().set("canteen:session:" + jti, objectMapper.writeValueAsString(session),
                    Duration.ofHours(2));
        } catch (JsonProcessingException exception){
            throw new IllegalStateException("登录会话保存失败", exception);
        }
        UserInfo safeUser = new UserInfo();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setNickname(user.getNickname());
        safeUser.setRole(user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");
        data.put("expiresIn", 7200);
        data.put("userInfo", safeUser);
        return data;
    }

    @Override
    public void logout() {
        Map<String,Object> map = threadLocalUtils.get();
        String jti = (String) map.get("jti");
        redisTemplate.delete("canteen:session:" + jti);
    }
}
