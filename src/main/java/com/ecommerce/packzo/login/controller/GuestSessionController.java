package com.ecommerce.packzo.login.controller;


import com.ecommerce.packzo.login.service.LoginService;
import com.ecommerce.packzo.response.GuestSessionDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
public class GuestSessionController {

    private final LoginService loginService;

    public GuestSessionController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/createSession")
    public GuestSessionDto createSession() {

        return loginService.createGuestSession();
    }
}
