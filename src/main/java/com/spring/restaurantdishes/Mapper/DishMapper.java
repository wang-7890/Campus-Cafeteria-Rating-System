package com.spring.restaurantdishes.Mapper;

import com.mybatisflex.core.BaseMapper;
import com.spring.restaurantdishes.model.dataBasePojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
