/*
package com.ecommerce.packzo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(User user){

        return Jwts.builder()

                .subject(user.getUserId())

                .claim("role", user.getRole())

                .issuedAt(new Date())

                .expiration(
                        new Date(System.currentTimeMillis()
                                + 900000))

                .signWith(getKey())

                .compact();
    }

    public String generateRefreshToken(User user){

        return UUID.randomUUID().toString();
    }
}
*/
