package com.ecommerce.packzo.login.security;


import com.ecommerce.packzo.login.service.LoginService;

import com.ecommerce.packzo.login.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsServiceImpl userDetailsService;

    private final LoginService guestSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * Try JWT Authentication
                 */
                if (authenticateJwt(request)) {

                    filterChain.doFilter(request, response);
                    return;
                }

                /*
                 * Try Guest Authentication
                 */
                if (authenticateGuest(request)) {

                    filterChain.doFilter(request, response);
                    return;
                }
            }

        } catch (Exception ex) {

            log.error("Authentication failed : {}", ex.getMessage(), ex);

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);

    }

    /**
     * JWT Authentication
     */
    private boolean authenticateJwt(HttpServletRequest request) {

        String token = extractJwt(request);

        if (!StringUtils.hasText(token)) {
            return false;
        }

        if (!jwtService.isTokenValid(token)) {
            return false;
        }

        String email = jwtService.extractEmail(token);

        CustomUserDetails user =
                (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        log.debug("JWT authenticated : {}", email);

        return true;

    }

    /**
     * Guest Authentication
     */
    private boolean authenticateGuest(HttpServletRequest request) {

        String guestToken = request.getHeader("X-Guest-Token");

        if (!StringUtils.hasText(guestToken)) {
            return false;
        }

        if (!guestSessionService.isValidGuestToken(guestToken)) {

            return false;
        }

        GuestPrincipal principal = guestSessionService.getGuestPrincipal(guestToken);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities());

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        log.debug("Guest authenticated : {}", guestToken);

        return true;

    }

    /**
     * Extract JWT
     */
    private String extractJwt(HttpServletRequest request) {

        String authorization =
                request.getHeader("Authorization");

        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);

    }

}