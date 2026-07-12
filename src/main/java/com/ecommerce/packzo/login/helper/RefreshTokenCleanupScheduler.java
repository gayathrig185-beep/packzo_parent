package com.ecommerce.packzo.login.helper;

import com.ecommerce.packzo.login.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanup() {

        refreshTokenService.cleanupExpiredTokens();

    }

}