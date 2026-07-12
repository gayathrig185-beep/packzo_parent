package com.ecommerce.packzo.login.security;

import io.jsonwebtoken.Claims;

public interface JwtUtil {

    Claims extractClaims(String token);

}