package com.ecommerce.packzo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name should not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name should not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Invalid mobile number")
        String mobile,

        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$",
                message = "Password must contain uppercase, lowercase, number, special character and be 8-20 characters long"
        )
        String password,

        @NotBlank
        String address,

        @NotBlank
        String pinCode

) {
}