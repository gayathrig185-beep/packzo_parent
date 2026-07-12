package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.login.security.CustomUserDetails;
import com.ecommerce.packzo.login.security.GuestPrincipal;

import com.ecommerce.packzo.product.service.interfaces.SecurityService;
import com.ecommerce.packzo.request.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    @Override
    public CurrentUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken) {

            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails user) {

            return CurrentUser.builder()
                    .guest(false)
                    .userId(user.getUserId())
                    .build();
        }

        if (principal instanceof GuestPrincipal guest) {

            return CurrentUser.builder()
                    .guest(true)
                    .guestToken(guest.getGuestToken())
                    .build();
        }

        throw new RuntimeException("Invalid principal");
    }
}