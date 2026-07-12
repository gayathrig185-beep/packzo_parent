package com.ecommerce.packzo.login.security;

import com.ecommerce.packzo.login.config.JwtProperties;
import com.ecommerce.packzo.login.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService implements JwtUtil {

    private final JwtProperties properties;

    private final SecretKey secretKey;

    public JwtService(JwtProperties properties) {

        this.properties = properties;

        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(properties.getSecret()));
    }


    public String generateAccessToken(User user) {

        return Jwts.builder()

                .subject(user.getUserId())

                .claim("email", user.getEmail())

                .claim("role", user.getRole().name())

                .claim("type", TokenType.ACCESS.name())

                .issuedAt(new Date())

                .expiration(new Date(

                        System.currentTimeMillis()

                                + properties.getAccessTokenExpiration()))

                .signWith(secretKey)

                .compact();
    }

    public String generateRefreshToken(User user) {

        return Jwts.builder()

                .subject(user.getUserId())

                .claim("type", TokenType.REFRESH.name())

                .issuedAt(new Date())

                .expiration(new Date(

                        System.currentTimeMillis()

                                + properties.getRefreshTokenExpiration()))

                .signWith(secretKey)

                .compact();
    }

    @Override
    public Claims extractClaims(String token) {

        return Jwts.parser()

                .verifyWith(secretKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }

    public String extractUserId(String token) {

        return extractClaims(token).getSubject();
    }


    public String extractEmail(String token) {

        return extractClaims(token)
                .get("email", String.class);
    }


    public TokenType extractTokenType(String token) {

        return TokenType.valueOf(

                extractClaims(token)

                        .get("type", String.class));
    }


    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractClaims(token);

            return claims.getExpiration().after(new Date());

        } catch (Exception ex) {

            return false;
        }

    }

}