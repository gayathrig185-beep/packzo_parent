package com.ecommerce.packzo.login.service;


import com.ecommerce.packzo.entity.RefreshToken;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.login.config.JwtProperties;
import com.ecommerce.packzo.login.constants.Role;
import com.ecommerce.packzo.login.entity.User;
import com.ecommerce.packzo.login.respository.UserRepository;
import com.ecommerce.packzo.login.security.JwtService;
import com.ecommerce.packzo.product.service.impl.CartService;
import com.ecommerce.packzo.request.LoginRequest;
import com.ecommerce.packzo.request.LogoutRequest;
import com.ecommerce.packzo.request.RefreshTokenRequest;
import com.ecommerce.packzo.request.SignupRequest;
import com.ecommerce.packzo.response.LoginResponse;
import com.ecommerce.packzo.response.RefreshTokenResponse;
import com.ecommerce.packzo.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserIdGenerator userIdGenerator;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    private final AuthenticationManager authenticationManager;

    private final CartService cartService;

    private final LoginService loginService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserIdGenerator userIdGenerator, RefreshTokenService refreshTokenService, JwtService jwtService, JwtProperties jwtProperties, AuthenticationManager authenticationManager, CartService cartService, LoginService loginService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userIdGenerator = userIdGenerator;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.authenticationManager = authenticationManager;
        this.cartService = cartService;
        this.loginService = loginService;
    }


    public SignupResponse signup(SignupRequest request) {

        LOGGER.info("Signup request received for email : {}", request.email());

        validateEmail(request.email());

        validateMobile(request.mobile());

        checkDuplicateUser(request);

        User user = buildUser(request);

        userRepository.save(user);

        LOGGER.info("User registered successfully : {}", user.getUserId());

        return new SignupResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getEmail(),
                user.getRole().name(),
                "User registered successfully");
    }

    /**
     * Email Validation
     */
    private void validateEmail(String email) {

        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String regex =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if(!email.matches(regex)) {
            throw new IllegalArgumentException("Invalid Email");
        }
    }

    /**
     * Mobile Validation
     */
    private void validateMobile(String mobile) {

        if(mobile == null || mobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number cannot be empty");
        }

        if(!mobile.matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException("Invalid Mobile Number");
        }

    }

    /**
     * Duplicate Validation
     */
    private void checkDuplicateUser(SignupRequest request) {

        if(userRepository.existsByEmailIgnoreCase(request.email())) {

            LOGGER.warn("Email already exists {}", request.email());

            throw new PaczoException("","",
                    "Email already registered");
        }

        if(userRepository.existsByMobile(request.mobile())) {

            LOGGER.warn("Mobile already exists {}", request.mobile());

            throw new PaczoException("","",
                    "Mobile number already registered");
        }

    }

    /**
     * Build User Entity
     */
    private User buildUser(SignupRequest request) {

        User user = new User();

        user.setUserId(userIdGenerator.generateUserId());

        user.setFirstName(request.firstName().trim());

        user.setLastName(request.lastName());

        user.setEmail(request.email().toLowerCase().trim());

        user.setMobile(request.mobile());

        // Password Encryption
        user.setPassword(passwordEncoder.encode(request.password()));

        user.setRole(Role.CUSTOMER);

        user.setActive(Boolean.TRUE);

        user.setAddress(request.address());

        user.setPinCode(request.pinCode());

        user.setEmailVerified(Boolean.FALSE);

        user.setMobileVerified(Boolean.FALSE);

        user.setCreatedBy("SYSTEM");

        user.setUpdatedBy("SYSTEM");

        return user;

    }


    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        LOGGER.info("Refreshing access token");

        /*
         * Step 1
         * Validate Refresh Token
         */
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(
                        request.refreshToken());

        /*
         * Step 2
         * Get User
         */
        User user = refreshToken.getUser();

        /*
         * Step 3
         * Generate Access Token
         */
        String accessToken = jwtService.generateAccessToken(user);

        /*
         * Step 4
         * Create New Refresh Token
         */
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user, refreshToken.getDeviceId(), refreshToken.getIpAddress(), refreshToken.getUserAgent());

        /*
         * Step 5
         * Revoke Old Token
         */
        refreshTokenService.revokeToken(refreshToken.getToken());

        LOGGER.info("Refresh token rotated successfully");

        /*
         * Step 6
         * Response
         */

        return new RefreshTokenResponse(accessToken, newRefreshToken.getToken(), "Bearer", jwtProperties.getAccessTokenExpiration());

    }


    public void logout(LogoutRequest request) {

        LOGGER.info("Logout initiated");

        refreshTokenService.revokeToken(request.refreshToken());

        LOGGER.info("Logout completed");
    }


    public void logoutAll(String userId) {

        LOGGER.info("Logout all devices for {}", userId);

        refreshTokenService.revokeAllTokens(userId);

        LOGGER.info("All devices logged out");
    }


    public LoginResponse login(LoginRequest request, HttpServletRequest servletRequest) {

        LOGGER.info("Login request for {}", request.email());

        // Step 1: Authenticate credentials
        authenticateUser(request);

        // Step 2: Load user
        User user = userRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Step 3: Generate JWT
        String accessToken = jwtService.generateAccessToken(user);

        // Step 4: Read request metadata
        String ipAddress = extractClientIp(servletRequest);

        String userAgent = servletRequest.getHeader("User-Agent");

        // Step 5: Create Refresh Token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, request.deviceId(), ipAddress, userAgent);

        // Step 6: Merge Guest Cart
        mergeGuestData(request.guestToken(), user);

        LOGGER.info("Login successful for {}", user.getUserId());

        // Step 7: Response
        return new LoginResponse(

                accessToken,

                refreshToken.getToken(),

                "Bearer",

                jwtProperties.getAccessTokenExpiration(),

                user.getUserId(),

                user.getFirstName(),

                user.getRole().name());

    }

    private void authenticateUser(LoginRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    }

    private void mergeGuestData(
            String guestToken,
            User user) {

        if (guestToken == null || guestToken.isBlank()) {
            return;
        }

        cartService.mergeGuestCart(guestToken, user.getUserId());

        loginService.deactivateGuestSession(guestToken);

    }

    private String extractClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }


}