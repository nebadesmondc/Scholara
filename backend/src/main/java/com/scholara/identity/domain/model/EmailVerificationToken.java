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
import java.util.UUID;

/**
 * Entity representing an email verification token (OTP).
 *
 * <p>When a user registers, an OTP is generated and sent to their email.
 * The user must enter this OTP to verify their email address.
 */
@Getter
@Setter
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken extends BaseEntity {

    private static final int OTP_VALIDITY_MINUTES = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 6)
    private String otpCode;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    /**
     * Protected constructor for JPA.
     */
    protected EmailVerificationToken() {
    }

    /**
     * Creates a new email verification token for a user.
     *
     * @param userId the user's ID
     * @return a new EmailVerificationToken with a random 6-digit OTP
     */
    public static EmailVerificationToken create(UUID userId) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.userId = userId;
        token.otpCode = generateOtp();
        token.expiresAt = Instant.now().plus(OTP_VALIDITY_MINUTES, ChronoUnit.MINUTES);
        token.used = false;
        return token;
    }

    /**
     * Generates a random 6-digit OTP.
     *
     * @return a 6-digit string OTP
     */
    private static String generateOtp() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
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
