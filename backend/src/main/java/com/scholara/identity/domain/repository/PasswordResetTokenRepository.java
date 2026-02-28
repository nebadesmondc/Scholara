package com.scholara.identity.domain.repository;

import com.scholara.identity.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PasswordResetToken entity persistence operations.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * Finds a valid (unused) password reset token by its hash.
     *
     * @param tokenHash the SHA-256 hash of the token
     * @return the token if found and not used
     */
    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

    /**
     * Deletes all expired password reset tokens.
     *
     * @param now the current timestamp
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Deletes all password reset tokens for a user.
     *
     * @param userId the user's ID
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    /**
     * Invalidates all existing password reset tokens for a user.
     *
     * @param userId the user's ID
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.userId = :userId AND t.used = false")
    void invalidateAllByUserId(@Param("userId") UUID userId);
}
