package com.spring.restaurantdishes.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.restaurantdishes.model.Result;
import com.spring.restaurantdishes.utils.JwtUtil;
import com.spring.restaurantdishes.utils.threadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.HashMap;
import java.util.Map;


@Component
public class loginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new RuntimeException("Authorization格式错误");
            }

            String token = header.substring(7).trim();

            // 先验证签名和过期时间
            DecodedJWT jwt = (DecodedJWT) JwtUtil.parseToken(token);
            String jti = jwt.getId();

            if (jti == null || jti.isBlank()) {
                throw new RuntimeException("Token缺少jti");
            }

            // 再检查Redis登录状态
            String session = redisTemplate.opsForValue().get(
                    "canteen:session:" + jti
            );

            if (session == null) {
                throw new RuntimeException("登录状态不存在或已过期");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", Long.valueOf(jwt.getSubject()));
            claims.put("role", jwt.getClaim("role").asString());
            claims.put("jti", jti);

            threadLocalUtils.set(claims);
            return true;

        } catch (Exception exception) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            Result.error(401, "请先登录")
                    )
            );
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocalUtils.remove();
    }
}
