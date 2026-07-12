package com.ecommerce.packzo.login.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {

    public String generateUserId() {

        return "USR-"
                + UUID.randomUUID()
                .toString()
                .substring(0,8)
                .toUpperCase();
    }

}