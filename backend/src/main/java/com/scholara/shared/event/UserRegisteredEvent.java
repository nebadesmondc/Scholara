package com.scholara.shared.event;

import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import com.scholara.shared.domain.UserId;

import java.time.Instant;

/**
 * Domain event published when a new user registers.
 *
 * <p>This event is consumed by modules that need to react to new user creation,
 * such as the notification module for sending welcome emails.
 */
public record UserRegisteredEvent(
        UserId userId,
        Email email,
        Role role,
        AuthProvider provider,
        Instant registeredAt
) {
    public UserRegisteredEvent {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (email == null) throw new IllegalArgumentException("email cannot be null");
        if (role == null) throw new IllegalArgumentException("role cannot be null");
        if (provider == null) throw new IllegalArgumentException("provider cannot be null");
        if (registeredAt == null) throw new IllegalArgumentException("registeredAt cannot be null");
    }
}
