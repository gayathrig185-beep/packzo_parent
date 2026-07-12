package com.ecommerce.packzo.config;

import java.util.List;
import com.ecommerce.packzo.login.security.JwtAuthenticationEntryPoint;
import com.ecommerce.packzo.login.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    private final JwtAuthenticationEntryPoint entryPoint;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->

                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception ->

                        exception.authenticationEntryPoint(
                                entryPoint))

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Public APIs
                         */

                        .requestMatchers(

                                "/api/auth/**",

                                "/api/guest/**",

                                "/swagger-ui/**",

                                "/v3/api-docs/**"

                        ).permitAll()

                        /*
                         * Product APIs
                         */

                        .requestMatchers(

                                "/api/catalog/**",

                                "/api/sectors/**",

                                "/api/products/**",

                                "/api/search/**",

                                "/api/suggestions/**"

                        ).permitAll()

                        /*
                         * Cart
                         */

                        .requestMatchers(

                                "/api/cart/**"

                        ).authenticated()

                        /*
                         * Orders
                         */

                        .requestMatchers(

                                "/api/orders/**"

                        ).authenticated()

                        /*
                         * Admin
                         */

                        .requestMatchers(

                                "/api/admin/**"

                        ).hasRole("ADMIN")

                        .anyRequest()

                        .authenticated()

                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(

                        jwtFilter,

                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200"));

        configuration.setAllowedMethods(
                List.of("GET","POST","PUT","DELETE","OPTIONS"));

        configuration.setAllowedHeaders(
                List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",
                configuration);

        return source;
    }
}