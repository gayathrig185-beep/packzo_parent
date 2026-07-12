package com.ecommerce.packzo.login.controller;

import com.ecommerce.packzo.login.security.CustomUserDetails;
import com.ecommerce.packzo.login.service.AuthService;
import com.ecommerce.packzo.login.service.RefreshTokenService;
import com.ecommerce.packzo.request.LoginRequest;
import com.ecommerce.packzo.request.LogoutRequest;
import com.ecommerce.packzo.request.RefreshTokenRequest;
import com.ecommerce.packzo.request.SignupRequest;
import com.ecommerce.packzo.response.LoginResponse;
import com.ecommerce.packzo.response.RefreshTokenResponse;
import com.ecommerce.packzo.response.SignupResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServlet) {

        return ResponseEntity.ok(authService.login(request, httpServlet));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {

        authService.logout(request);

        return ResponseEntity.ok("Logout SuccessFully");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        authService.logoutAll(user.getUserId());

        return ResponseEntity.ok("Logged out from all devices");
    }
}