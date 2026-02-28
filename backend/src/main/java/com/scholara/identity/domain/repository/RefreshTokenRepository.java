package com.scholara.identity.domain.repository;

import com.scholara.identity.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RefreshToken entity persistence operations.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Finds a valid (not revoked) refresh token by its hash.
     *
     * @param tokenHash the SHA-256 hash of the token
     * @return the token if found and not revoked
     */
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    /**
     * Revokes all refresh tokens for a user.
     *
     * @param userId the user's ID
     */
    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.userId = :userId")
    void revokeAllByUserId(@Param("userId") UUID userId);

    /**
     * Deletes all expired refresh tokens.
     *
     * @param now the current timestamp
     */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Counts active (not revoked, not expired) tokens for a user.
     *
     * @param userId the user's ID
     * @param now the current timestamp
     * @return the count of active tokens
     */
    @Query("SELECT COUNT(t) FROM RefreshToken t WHERE t.userId = :userId AND t.revoked = false AND t.expiresAt > :now")
    long countActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") Instant now);
}
