package com.ecommerce.packzo.login.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecommerce.packzo.entity.RefreshToken;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.login.config.JwtProperties;
import com.ecommerce.packzo.login.entity.User;
import com.ecommerce.packzo.login.respository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProperties jwtProperties;


    public RefreshToken createRefreshToken(
            User user,
            String deviceId,
            String ipAddress,
            String userAgent) {

        LOGGER.info("Creating refresh token for user : {}",
                user.getUserId());

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(generateRefreshToken());

        refreshToken.setUser(user);

        refreshToken.setDeviceId(deviceId);

        refreshToken.setIpAddress(ipAddress);

        refreshToken.setUserAgent(userAgent);

        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusSeconds(
                                jwtProperties
                                        .getRefreshTokenExpiration() / 1000));

        refreshToken.setRevoked(false);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        LOGGER.info("Refresh token created successfully : {}",
                savedToken.getId());

        return savedToken;
    }

    public String generateRefreshToken() {

        return UUID.randomUUID().toString();

    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {

        LOGGER.info("Validating refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {

                    LOGGER.warn("Refresh token not found");

                    return new PaczoException("","",
                            "Invalid refresh token");
                });

        validateRevoked(refreshToken);

        validateExpiry(refreshToken);

        validateUser(refreshToken);

        LOGGER.info("Refresh token validated successfully");

        return refreshToken;
    }

    /**
     * Check revoked
     */
    private void validateRevoked(
            RefreshToken refreshToken) {

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {

            LOGGER.warn("Refresh token revoked");

            throw new PaczoException("","", "Refresh token has been revoked");
        }

    }

    /**
     * Check expiry
     */
    private void validateExpiry(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);

            LOGGER.warn("Refresh token expired");

            throw new PaczoException("","", "Refresh token expired");
        }

    }

    /**
     * User validation
     */
    private void validateUser(
            RefreshToken refreshToken) {

        if (!Boolean.TRUE.equals(
                refreshToken.getUser().getActive())) {

            LOGGER.warn("Inactive user");

            throw new PaczoException("","", "User account is inactive");
        }

    }


    @Transactional
    public void revokeToken(String token) {

        LOGGER.info("Revoking refresh token");

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new PaczoException("","", "Refresh token not found"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);

        LOGGER.info("Refresh token revoked successfully");
    }

    @Transactional
    public void revokeAllTokens(String userId) {

        LOGGER.info("Revoking all refresh tokens for user {}", userId);

        List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserUserIdAndRevokedFalse(userId);

        refreshTokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokens);

        LOGGER.info("{} refresh tokens revoked", refreshTokens.size());
    }

    @Transactional
    public void cleanupExpiredTokens() {

        LOGGER.info("Cleaning expired refresh tokens");

        long deleted = refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        LOGGER.info("{} expired tokens deleted", deleted);
    }

}