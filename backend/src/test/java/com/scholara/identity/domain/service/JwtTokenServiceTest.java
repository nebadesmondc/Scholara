package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.exception.TokenExpiredException;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.infrastructure.config.JwtProperties;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("6yB+7S2vN9pX4qR5tW8zB3mE6nJ8kL1uP4sV7xY0zB2=");
        jwtProperties.setAccessTokenExpiration(Duration.ofMinutes(15));
        jwtProperties.setRefreshTokenExpiration(Duration.ofDays(7));

        jwtTokenService = new JwtTokenService(jwtProperties);
    }

    @Test
    void generateAccessToken_shouldCreateValidToken() {
        User user = createTestUser();

        String token = jwtTokenService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
    }

    @Test
    void validateToken_withValidToken_shouldReturnClaims() {
        User user = createTestUser();
        String token = jwtTokenService.generateAccessToken(user);

        JwtTokenService.JwtClaims claims = jwtTokenService.validateToken(token);

        assertEquals(user.getId(), claims.userId());
        assertEquals(user.getEmail(), claims.email());
        assertEquals(user.getRole(), claims.role());
        assertNotNull(claims.expiration());
    }

    @Test
    void validateToken_withExpiredToken_shouldThrowTokenExpiredException() throws InterruptedException {
        // Create service with very short expiry
        JwtProperties shortExpiry = new JwtProperties();
        shortExpiry.setSecret("6yB+7S2vN9pX4qR5tW8zB3mE6nJ8kL1uP4sV7xY0zB2=");
        shortExpiry.setAccessTokenExpiration(Duration.ofMillis(1));
        JwtTokenService shortExpiryService = new JwtTokenService(shortExpiry);

        User user = createTestUser();
        String token = shortExpiryService.generateAccessToken(user);

        // Wait for token to expire
        Thread.sleep(50);

        assertThrows(TokenExpiredException.class, () -> shortExpiryService.validateToken(token));
    }

    @Test
    void validateToken_withInvalidToken_shouldThrowInvalidTokenException() {
        assertThrows(InvalidTokenException.class, () -> jwtTokenService.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_withTamperedToken_shouldThrowInvalidTokenException() {
        User user = createTestUser();
        String token = jwtTokenService.generateAccessToken(user);

        // Tamper with the token
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(InvalidTokenException.class, () -> jwtTokenService.validateToken(tamperedToken));
    }

    @Test
    void generateAccessToken_shouldIncludeCorrectClaims() {
        User user = createTestUser();

        String token = jwtTokenService.generateAccessToken(user);
        JwtTokenService.JwtClaims claims = jwtTokenService.validateToken(token);

        assertEquals(user.getId(), claims.userId());
        assertEquals(user.getEmail(), claims.email());
        assertEquals(Role.STUDENT, claims.role());
    }

    private User createTestUser() {
        User user = User.createLocalUser(
                Email.of("test@example.com"),
                "hashedPassword",
                Role.STUDENT
        );
        setUserId(user, UUID.randomUUID());
        return user;
    }

    private void setUserId(User user, UUID id) {
        try {
            Field idField = user.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set user ID for test", e);
        }
    }
}
