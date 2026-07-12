package com.ecommerce.packzo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUser {

    private String userId;

    private String guestToken;

    private boolean guest;
}