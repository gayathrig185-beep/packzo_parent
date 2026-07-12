package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.request.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserContextResolver {

    public UserContext resolve(Authentication authentication,
                               String guestToken){

        if(authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())){

            return UserContext.builder()
                    .authenticated(true)
                    .guest(false)
                    .userId(authentication.getName())
                    .build();
        }

        return UserContext.builder()
                .guest(true)
                .authenticated(false)
                .guestToken(guestToken)
                .build();
    }
}