package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.exception.TokenExpiredException;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.infrastructure.config.JwtProperties;
import com.scholara.shared.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Service for generating and validating JWT access tokens.
 */
@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates an access token for a user.
     *
     * @param user the user to generate the token for
     * @return the JWT access token string
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates a JWT token and extracts its claims.
     *
     * @param token the JWT token string
     * @return the extracted claims
     * @throws TokenExpiredException if the token has expired
     * @throws InvalidTokenException if the token is invalid
     */
    public JwtClaims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new JwtClaims(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    Role.valueOf(claims.get("role", String.class)),
                    claims.getExpiration().toInstant()
            );
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Access token has expired");
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid access token", e);
        }
    }

    /**
     * Extracted claims from a JWT token.
     */
    public record JwtClaims(
            UUID userId,
            String email,
            Role role,
            Instant expiration
    ) {
    }
}
