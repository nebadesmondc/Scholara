package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.EmailAlreadyExistsException;
import com.scholara.identity.domain.exception.UserNotFoundException;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import com.scholara.shared.domain.UserId;
import com.scholara.shared.event.PasswordChangedEvent;
import com.scholara.shared.event.UserRegisteredEvent;
import com.scholara.shared.event.UserVerifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for user management operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registers a new local user with email/password authentication.
     *
     * @param email the user's email
     * @param rawPassword the raw password
     * @param role the user's role
     * @return the created user
     * @throws EmailAlreadyExistsException if the email is already registered
     */
    public User registerLocalUser(Email email, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email.value())) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = User.createLocalUser(email, passwordHash, role);
        user = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                user.userId(),
                email,
                role,
                AuthProvider.LOCAL,
                Instant.now()
        ));

        return user;
    }

    /**
     * Finds or creates an OAuth user.
     *
     * @param email the user's email from OAuth provider
     * @param provider the OAuth provider
     * @param providerId the provider-specific user ID
     * @param defaultRole the role to assign if creating a new user
     * @return the existing or newly created user
     */
    public User findOrCreateOAuthUser(Email email, AuthProvider provider,
                                       String providerId, Role defaultRole) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // Check if email already exists with different provider
                    Optional<User> existingUser = userRepository.findByEmail(email.value());
                    if (existingUser.isPresent()) {
                        // For now, we don't allow linking accounts
                        // User must use their original auth method
                        throw new EmailAlreadyExistsException(email);
                    }

                    User user = User.createOAuthUser(email, defaultRole, provider, providerId);
                    user = userRepository.save(user);

                    eventPublisher.publishEvent(new UserRegisteredEvent(
                            user.userId(),
                            email,
                            defaultRole,
                            provider,
                            Instant.now()
                    ));

                    return user;
                });
    }

    /**
     * Verifies a user's email and enables their account.
     *
     * @param user the user to verify
     */
    public void verifyEmail(User user) {
        user.verifyEmail();
        userRepository.save(user);

        eventPublisher.publishEvent(new UserVerifiedEvent(
                user.userId(),
                Email.of(user.getEmail()),
                Instant.now()
        ));
    }

    /**
     * Changes a user's password.
     *
     * @param user the user
     * @param newRawPassword the new raw password
     */
    public void changePassword(User user, String newRawPassword) {
        String newHash = passwordEncoder.encode(newRawPassword);
        user.changePassword(newHash);
        userRepository.save(user);

        eventPublisher.publishEvent(new PasswordChangedEvent(
                user.userId(),
                Instant.now()
        ));
    }

    /**
     * Records a login event for a user.
     *
     * @param user the user
     */
    public void recordLogin(User user) {
        user.recordLogin();
        userRepository.save(user);
    }

    /**
     * Gets a user by ID or throws an exception.
     *
     * @param userId the user ID
     * @return the user
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User getById(UserId userId) {
        return userRepository.findById(userId.value())
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Gets a user by email or throws an exception.
     *
     * @param email the email
     * @return the user
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User getByEmail(Email email) {
        return userRepository.findByEmail(email.value())
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email
     * @return the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(Email email) {
        return userRepository.findByEmail(email.value());
    }
}
