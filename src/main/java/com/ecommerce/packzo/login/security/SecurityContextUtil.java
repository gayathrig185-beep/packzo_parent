package com.ecommerce.packzo.login.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAuthenticated() {

        Authentication authentication = getAuthentication();

        return authentication != null
                && authentication.isAuthenticated();
    }

    public boolean isGuest() {

        Authentication authentication = getAuthentication();

        return authentication != null
                && authentication.getPrincipal() instanceof GuestPrincipal;
    }

    public String getUserId() {

        Authentication authentication = getAuthentication();

        if (authentication != null &&
            authentication.getPrincipal() instanceof CustomUserDetails user) {

            return user.getUserId();
        }

        return null;
    }

    public String getGuestToken() {

        Authentication authentication = getAuthentication();

        if (authentication != null &&
            authentication.getPrincipal() instanceof GuestPrincipal guest) {

            return guest.getName();
        }

        return null;
    }
}