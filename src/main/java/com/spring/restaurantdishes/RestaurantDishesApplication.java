package com.spring.restaurantdishes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan(basePackages = "com.spring.restaurantdishes.Mapper")
@SpringBootApplication
public class RestaurantDishesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantDishesApplication.class, args);
    }

}
