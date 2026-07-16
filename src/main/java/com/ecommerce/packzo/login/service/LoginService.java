package com.ecommerce.packzo.login.service;

import com.ecommerce.packzo.entity.GuestSession;
import com.ecommerce.packzo.entity.RefreshToken;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.login.config.JwtProperties;
import com.ecommerce.packzo.login.constants.GuestSessionStatus;
import com.ecommerce.packzo.login.entity.User;
import com.ecommerce.packzo.login.respository.GuestRespository;

import com.ecommerce.packzo.login.respository.UserRepository;
import com.ecommerce.packzo.login.security.GuestPrincipal;
import com.ecommerce.packzo.login.security.JwtService;
import com.ecommerce.packzo.product.service.impl.CartService;
import com.ecommerce.packzo.request.LoginRequest;
import com.ecommerce.packzo.response.GuestSessionDto;
import com.ecommerce.packzo.response.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final GuestRespository guestRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CartService cartService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;


    public LoginService(GuestRespository guestRepository, UserRepository userRepository, JwtService jwtService, RefreshTokenService refreshTokenService, CartService cartService, JwtProperties jwtProperties, AuthenticationManager authenticationManager) {
        this.guestRepository = guestRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cartService = cartService;
        this.jwtProperties = jwtProperties;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public GuestSessionDto createGuestSession() {

        String token = UUID.randomUUID().toString();

        GuestSession guest = new GuestSession();

        guest.setGuestToken(token);
        guest.setCreatedAt(LocalDateTime.now());
        guest.setExpiresAt(LocalDateTime.now().plusDays(30));
        guest.setActive(true);
        guest.setStatus(GuestSessionStatus.ACTIVE);
        guestRepository.save(guest);

        return GuestSessionDto.builder()
                .guestToken(token)
                .createdAt(guest.getCreatedAt())
                .expiresAt(guest.getExpiresAt())
                .active(true)
                .build();
    }


    public boolean isValidGuestToken(String guestToken) {

        return guestRepository.findByGuestTokenAndActiveTrue(guestToken).isPresent();
    }

    public GuestPrincipal getGuestPrincipal(String guestToken) {

        return new GuestPrincipal(guestToken);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, request.deviceId(), request.ipAddress(), request.userAgent());

        if (request.guestToken() != null) {

            cartService.mergeGuestCart(request.guestToken(), user.getUserId());

        }

        return new LoginResponse(

                accessToken,

                refreshToken.getToken(),

                "Bearer",

                jwtProperties.getAccessTokenExpiration(),

                user.getUserId(),

                user.getFirstName(),

                user.getRole().name());
    }


    public void deactivateGuestSession(String guestToken) {

        LOGGER.info("Deactivating guest session : {}", guestToken);

        GuestSession guestSession =
                guestRepository
                        .findByGuestTokenAndStatus(
                                guestToken,
                                GuestSessionStatus.ACTIVE)
                        .orElseThrow(() ->
                                new PaczoException("","", "Guest session not found"));

        guestSession.setStatus(GuestSessionStatus.MERGED);

        guestSession.setDeactivatedAt(LocalDateTime.now());

        guestSession.setDeactivationReason("USER_LOGIN");

        guestRepository.save(guestSession);

        LOGGER.info("Guest session deactivated successfully");

    }
}
