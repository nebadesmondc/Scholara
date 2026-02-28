package com.scholara.identity.api.dto;

import com.scholara.identity.domain.model.User;
import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Role;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for user information.
 */
public record UserResponse(
        UUID id,
        String email,
        Role role,
        AuthProvider provider,
        boolean emailVerified,
        ZonedDateTime createdAt
) {
    /**
     * Creates a UserResponse from a User entity.
     *
     * @param user the user entity
     * @return the response DTO
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getProvider(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
