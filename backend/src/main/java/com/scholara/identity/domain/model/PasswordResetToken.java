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
 * Entity representing a password reset token.
 *
 * <p>When a user requests a password reset, a token is generated and
 * sent to their email. The token is valid for a limited time.
 */
@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends BaseEntity {

    private static final int TOKEN_VALIDITY_HOURS = 1;
    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    /**
     * Protected constructor for JPA.
     */
    protected PasswordResetToken() {
    }

    /**
     * Creates a new password reset token for a user.
     *
     * @param userId the user's ID
     * @param tokenHash the SHA-256 hash of the raw token
     * @return a new PasswordResetToken
     */
    public static PasswordResetToken create(UUID userId, String tokenHash) {
        PasswordResetToken token = new PasswordResetToken();
        token.userId = userId;
        token.tokenHash = tokenHash;
        token.expiresAt = Instant.now().plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS);
        token.used = false;
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
     * Checks if this token is still valid (not used and not expired).
     *
     * @return true if the token can be used
     */
    public boolean isValid() {
        return !used && Instant.now().isBefore(expiresAt);
    }

    /**
     * Marks this token as used.
     */
    public void markUsed() {
        this.used = true;
    }
}
