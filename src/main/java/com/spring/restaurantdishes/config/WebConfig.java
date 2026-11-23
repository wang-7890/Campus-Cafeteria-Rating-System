package com.spring.restaurantdishes.config;

import com.spring.restaurantdishes.Interceptor.loginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Component
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private loginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "/api/auth/login",
                "/api/auth/register",
                "/api/dishes",
                "/api/dishes/hot",
                "/api/dishes/{id}",
                "/api/dishes/{id}/reviews",
                "/api/dishes/{id}/ai-summary"
        );
    }
}
