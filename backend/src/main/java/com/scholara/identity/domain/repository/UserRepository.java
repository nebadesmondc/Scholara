package com.scholara.identity.domain.repository;

import com.scholara.identity.domain.model.User;
import com.scholara.shared.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address
     * @return the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email address
     * @return true if a user exists with this email
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their OAuth provider and provider-specific ID.
     *
     * @param provider the OAuth provider
     * @param providerId the provider-specific user ID
     * @return the user if found
     */
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
