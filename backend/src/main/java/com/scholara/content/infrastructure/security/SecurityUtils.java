package com.scholara.content.infrastructure.security;

import com.scholara.shared.domain.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility for extracting security information in the content module.
 *
 * <p>This avoids direct dependency on the identity module's ScholaraPrincipal
 * while still being able to extract the UserId.
 */
@Component
public class SecurityUtils {

    /**
     * Gets the current user's ID from the security context.
     *
     * @return the current user's UserId
     * @throws IllegalStateException if no user is authenticated
     */
    public UserId getCurrentUserId() {
        return tryGetCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    /**
     * Attempts to get the current user's ID.
     *
     * @return an Optional containing the UserId, or empty if not authenticated
     */
    public Optional<UserId> tryGetCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserId) {
            return Optional.of((UserId) principal);
        }
        if (principal instanceof UUID) {
            return Optional.of(UserId.of((UUID) principal));
        }

        for (String methodName : new String[]{"getUserId", "userId", "getId"}) {
            try {
                assert principal != null;
                Method method = principal.getClass().getMethod(methodName);
                Object result = method.invoke(principal);

                if (result instanceof UserId) {
                    return Optional.of((UserId) result);
                } else if (result instanceof UUID) {
                    return Optional.of(UserId.of((UUID) result));
                } else if (result instanceof String) {
                    try {
                        return Optional.of(UserId.of((String) result));
                    } catch (IllegalArgumentException _) {
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // Method not found or not accessible, try next
            }
        }

        // try to parse authentication name as UUID
        try {
            return Optional.of(UserId.of(authentication.getName()));
        } catch (IllegalArgumentException | NullPointerException e) {
            // Not a UUID
        }

        return Optional.empty();
    }
}
