package com.ecommerce.packzo.response;

public record SignupResponse(

        String userId,

        String firstName,

        String email,

        String role,

        String message

) {
}