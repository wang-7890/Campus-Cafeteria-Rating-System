package com.spring.restaurantdishes.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String KEY = "local-dev-only-change-this-to-a-random-secret-at-least-32-bytes";

    public static String genToken(Number userId,String role,String jti) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("role", role)
                .withJWTId(jti)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .sign(Algorithm.HMAC256(KEY));
    }

    public static DecodedJWT parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build().verify(token);
    }
}