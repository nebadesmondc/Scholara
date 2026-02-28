package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.model.RefreshToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.RefreshTokenRepository;
import com.scholara.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

/**
 * Service for managing refresh tokens.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Creates a new refresh token for a user.
     *
     * @param user the user
     * @param deviceInfo optional device information
     * @return the raw refresh token (to be sent to client)
     */
    public String createRefreshToken(User user, String deviceInfo) {
        String rawToken = RefreshToken.generateRawToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), tokenHash, deviceInfo);
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    /**
     * Creates a new refresh token for a user without device info.
     *
     * @param user the user
     * @return the raw refresh token
     */
    public String createRefreshToken(User user) {
        return createRefreshToken(user, null);
    }

    /**
     * Validates a refresh token and returns the associated token entity.
     *
     * @param rawToken the raw refresh token
     * @return the RefreshToken entity
     * @throws InvalidTokenException if the token is invalid or expired
     */
    @Transactional(readOnly = true)
    public RefreshToken validateAndGet(String rawToken) {
        String tokenHash = hashToken(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!token.isValid()) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        return token;
    }

    /**
     * Revokes a refresh token.
     *
     * @param rawToken the raw refresh token
     */
    public void revoke(String rawToken) {
        String tokenHash = hashToken(rawToken);

        refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * Revokes all refresh tokens for a user.
     *
     * @param userId the user's ID
     */
    public void revokeAllForUser(UserId userId) {
        refreshTokenRepository.revokeAllByUserId(userId.value());
    }

    /**
     * Scheduled task to clean up expired refresh tokens.
     * Runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
    }

    /**
     * Hashes a raw token using SHA-256.
     *
     * @param rawToken the raw token
     * @return the Base64-encoded hash
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
