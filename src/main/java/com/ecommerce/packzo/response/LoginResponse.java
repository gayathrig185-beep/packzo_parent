package com.ecommerce.packzo.response;

public record LoginResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        Long expiresIn,

        String userId,

        String firstName,

        String role

) {
}