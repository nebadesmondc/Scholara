package com.scholara.shared.event;

import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.UserId;

import java.time.Instant;

/**
 * Domain event published when a user verifies their email address.
 *
 * <p>This event indicates the user has completed the verification process
 * and their account is now active.
 */
public record UserVerifiedEvent(
        UserId userId,
        Email email,
        Instant verifiedAt
) {
    public UserVerifiedEvent {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (email == null) throw new IllegalArgumentException("email cannot be null");
        if (verifiedAt == null) throw new IllegalArgumentException("verifiedAt cannot be null");
    }
}
