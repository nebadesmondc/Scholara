package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.exception.UserNotFoundException;
import com.scholara.identity.domain.model.PasswordResetToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.PasswordResetTokenRepository;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.UserId;
import com.scholara.shared.event.SendPasswordResetEmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

/**
 * Service for password reset operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Initiates a password reset for a user.
     *
     * <p>If the email exists and belongs to a local user, creates a reset token
     * and publishes an event to send the reset email.
     *
     * @param email the user's email
     */
    public void initiatePasswordReset(Email email) {
        userRepository.findByEmail(email.value()).ifPresent(user -> {
            // Only allow password reset for local users
            if (user.getProvider() != AuthProvider.LOCAL) {
                return;
            }

            // Invalidate any existing reset tokens
            tokenRepository.invalidateAllByUserId(user.getId());

            String rawToken = PasswordResetToken.generateRawToken();
            String tokenHash = hashToken(rawToken);

            PasswordResetToken resetToken = PasswordResetToken.create(user.getId(), tokenHash);
            tokenRepository.save(resetToken);

            // Publish event for notification module to send email
            eventPublisher.publishEvent(new SendPasswordResetEmailEvent(
                    user.userId(),
                    email,
                    rawToken
            ));
        });
    }

    /**
     * Resets a user's password using a reset token.
     *
     * @param rawToken the raw reset token
     * @param newPassword the new password
     * @throws InvalidTokenException if the token is invalid or expired
     */
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = hashToken(rawToken);

        PasswordResetToken token = tokenRepository.findByTokenHashAndUsedFalse(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        if (!token.isValid()) {
            throw new InvalidTokenException("Reset token has expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        token.markUsed();
        tokenRepository.save(token);

        userService.changePassword(user, newPassword);

        // Revoke all refresh tokens (log out from all devices)
        refreshTokenService.revokeAllForUser(UserId.of(user.getId()));
    }

    /**
     * Scheduled task to clean up expired password reset tokens.
     * Runs every two hours.
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
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
