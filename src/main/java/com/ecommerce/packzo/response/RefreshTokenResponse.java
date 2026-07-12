package com.ecommerce.packzo.response;

public record RefreshTokenResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        Long expiresIn

) {
}