package com.ecommerce.packzo.login.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class GuestAuthenticationToken extends AbstractAuthenticationToken {

    private final GuestPrincipal principal;

    public GuestAuthenticationToken(GuestPrincipal principal) {

        super(principal.getAuthorities());

        this.principal = principal;

        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public GuestPrincipal getPrincipal() {
        return principal;
    }

}