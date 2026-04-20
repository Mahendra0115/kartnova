package com.kartnova.auth.security;

import com.kartnova.auth.util.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim(AppConstants.CLAIM_USER_ID, userId)
                .claim(AppConstants.CLAIM_ROLES, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long userId, String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim(AppConstants.CLAIM_USER_ID, userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTokenExpirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get(AppConstants.CLAIM_USER_ID);

        if (userId instanceof Integer value) {
            return value.longValue();
        }
        if (userId instanceof Long value) {
            return value;
        }
        return Long.parseLong(String.valueOf(userId));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = extractAllClaims(token).get(AppConstants.CLAIM_ROLES);

        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(String::valueOf)
                    .toList();
        }
        return Collections.emptyList();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public LocalDateTime getRefreshTokenExpiryDateTime() {
        return LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}