package com.spring.restaurantdishes.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.spring.restaurantdishes.Mapper.userMapper;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.model.userInfo;
import com.spring.restaurantdishes.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userServiceImpl implements userService {
    @Autowired
    private userMapper userMapper;
    @Override
    public Result register(userInfo user) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(userInfo::getUsername).eq(user.getUsername());
        userInfo userInfo = userMapper.selectOneByQuery(queryWrapper);
        if(userInfo!=null){
            return Result.error409();
        }
        else {
            userInfo newUser =new userInfo();
            newUser.setUsername(user.getUsername().strip());
            newUser.setNickname(user.getNickname());
            newUser.setRole("USER");
            userMapper.insert(newUser);
            return Result.success();
        }
    }
}
