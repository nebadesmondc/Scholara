package com.scholara.identity.domain.repository;

import com.scholara.identity.domain.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for EmailVerificationToken entity persistence operations.
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    /**
     * Finds a valid (unused) verification token by user ID and OTP code.
     *
     * @param userId the user's ID
     * @param otpCode the OTP code
     * @return the token if found and not used
     */
    Optional<EmailVerificationToken> findByUserIdAndOtpCodeAndUsedFalse(UUID userId, String otpCode);

    /**
     * Finds the most recent verification token for a user.
     *
     * @param userId the user's ID
     * @return the most recent token if found
     */
    Optional<EmailVerificationToken> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Deletes all expired verification tokens.
     *
     * @param now the current timestamp
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Deletes all verification tokens for a user.
     *
     * @param userId the user's ID
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
