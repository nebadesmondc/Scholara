package com.scholara.shared.event;

import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.UserId;

/**
 * Domain event requesting the notification module to send a password reset email.
 *
 * <p>This event decouples the identity module from email sending concerns.
 * The notification module handles the actual email delivery with the reset link.
 */
public record SendPasswordResetEmailEvent(
        UserId userId,
        Email email,
        String resetToken
) {
    public SendPasswordResetEmailEvent {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (email == null) throw new IllegalArgumentException("email cannot be null");
        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException("resetToken cannot be null or blank");
        }
    }
}
