package com.scholara.shared.event;

import com.scholara.shared.domain.UserId;

import java.time.Instant;

/**
 * Domain event published when a user changes their password.
 *
 * <p>This event can be used to notify users about password changes
 * for security purposes or to invalidate existing sessions.
 */
public record PasswordChangedEvent(
        UserId userId,
        Instant changedAt
) {
    public PasswordChangedEvent {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (changedAt == null) throw new IllegalArgumentException("changedAt cannot be null");
    }
}
