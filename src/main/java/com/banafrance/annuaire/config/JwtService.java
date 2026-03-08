package com.banafrance.annuaire.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private static final long PASSWORD_RESET_EXPIRATION_MS = 900_000; // 15 min

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(UUID userId, String email, String role) {
        return buildToken(email, accessTokenExpirationMs, "access", userId, role);
    }

    public String generateRefreshToken(UUID userId, String email) {
        return buildToken(email, refreshTokenExpirationMs, "refresh", userId, null);
    }

    public String generatePasswordResetToken(String email) {
        return buildToken(email, PASSWORD_RESET_EXPIRATION_MS, "password-reset", null, null);
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    private String buildToken(String subject, long expirationMs, String type, UUID userId, String role) {
        var builder = Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key);

        if (userId != null) {
            builder.claim("userId", userId.toString());
        }
        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
