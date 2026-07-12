package com.ecommerce.packzo.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserContext {

    private String userId;

    private String guestToken;

    private boolean guest;

    private boolean authenticated;
}