package com.scholara.identity.domain.model;

import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

/**
 * Entity representing a refresh token for maintaining user sessions.
 *
 * <p>Refresh tokens are long-lived tokens that can be used to obtain
 * new access tokens without re-authentication. They are stored hashed
 * for security.
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    private static final int TOKEN_VALIDITY_DAYS = 7;
    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(length = 500)
    private String deviceInfo;

    /**
     * Protected constructor for JPA.
     */
    protected RefreshToken() {
    }

    /**
     * Creates a new refresh token for a user.
     *
     * @param userId the user's ID
     * @param tokenHash the SHA-256 hash of the raw token
     * @param deviceInfo optional device/browser information
     * @return a new RefreshToken
     */
    public static RefreshToken create(UUID userId, String tokenHash, String deviceInfo) {
        RefreshToken token = new RefreshToken();
        token.userId = userId;
        token.tokenHash = tokenHash;
        token.expiresAt = Instant.now().plus(TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);
        token.revoked = false;
        token.deviceInfo = deviceInfo;
        return token;
    }

    /**
     * Generates a cryptographically secure random token.
     *
     * @return a Base64-encoded random token
     */
    public static String generateRawToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Checks if this token is still valid (not revoked and not expired).
     *
     * @return true if the token can be used
     */
    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    /**
     * Revokes this refresh token.
     */
    public void revoke() {
        this.revoked = true;
    }
}
