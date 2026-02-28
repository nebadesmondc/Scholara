package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.InvalidOtpException;
import com.scholara.identity.domain.exception.UserNotFoundException;
import com.scholara.identity.domain.model.EmailVerificationToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.EmailVerificationTokenRepository;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for email verification operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Creates a new verification token (OTP) for a user.
     *
     * @param user the user
     * @return the created token
     */
    public EmailVerificationToken createVerificationToken(User user) {
        // Invalidate any existing tokens
        tokenRepository.deleteByUserId(user.getId());

        EmailVerificationToken token = EmailVerificationToken.create(user.getId());
        return tokenRepository.save(token);
    }

    /**
     * Verifies an email using the OTP code.
     *
     * @param userId the user's ID
     * @param otpCode the OTP code
     * @throws InvalidOtpException if the OTP is invalid or expired
     * @throws UserNotFoundException if the user is not found
     */
    public void verifyEmail(UUID userId, String otpCode) {
        EmailVerificationToken token = tokenRepository
                .findByUserIdAndOtpCodeAndUsedFalse(userId, otpCode)
                .orElseThrow(() -> new InvalidOtpException("Invalid verification code"));

        if (!token.isValid()) {
            throw new InvalidOtpException("Verification code has expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserId.of(userId)));

        if (user.isEmailVerified()) {
            // Already verified, just mark token as used
            token.markUsed();
            tokenRepository.save(token);
            return;
        }

        token.markUsed();
        tokenRepository.save(token);

        userService.verifyEmail(user);
    }

    /**
     * Resends a verification email by creating a new token.
     *
     * @param user the user
     * @return the new token
     */
    public EmailVerificationToken resendVerification(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        return createVerificationToken(user);
    }

    /**
     * Scheduled task to clean up expired verification tokens.
     * Runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }
}
