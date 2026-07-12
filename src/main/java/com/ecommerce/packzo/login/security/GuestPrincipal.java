package com.ecommerce.packzo.login.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

public class GuestPrincipal implements Principal, Serializable {

    private final String guestToken;

    public GuestPrincipal(String guestToken) {
        this.guestToken = guestToken;
    }

    @Override
    public String getName() {
        return guestToken;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority("ROLE_GUEST"));
    }

    public boolean isGuest() {
        return true;
    }

}