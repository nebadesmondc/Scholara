package com.scholara.identity.domain.model;

import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import com.scholara.shared.domain.UserId;
import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * User aggregate root representing a platform user.
 *
 * <p>Users can authenticate via local credentials (email/password) or
 * through OAuth2 providers (Google). The user entity encapsulates all
 * identity and authentication state.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(length = 255)
    private String providerId;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean emailVerified;

    private Instant lastLoginAt;

    /**
     * Protected constructor for JPA.
     */
    protected User() {
    }

    /**
     * Creates a new local user with email/password authentication.
     *
     * <p>The user starts in a disabled state and must verify their email
     * before they can log in.
     *
     * @param email the user's email address
     * @param passwordHash the BCrypt hashed password
     * @param role the user's role
     * @return a new unverified User
     */
    public static User createLocalUser(Email email, String passwordHash, Role role) {
        User user = new User();
        user.email = email.value();
        user.password = passwordHash;
        user.role = role;
        user.provider = AuthProvider.LOCAL;
        user.providerId = null;
        user.enabled = false;
        user.emailVerified = false;
        return user;
    }

    /**
     * Creates a new OAuth user.
     *
     * <p>OAuth users are automatically enabled and email-verified as the
     * provider has already verified their identity.
     *
     * @param email the user's email from the OAuth provider
     * @param role the user's role (default STUDENT for new OAuth users)
     * @param provider the OAuth provider
     * @param providerId the unique ID from the OAuth provider
     * @return a new verified User
     */
    public static User createOAuthUser(Email email, Role role,
                                        AuthProvider provider, String providerId) {
        User user = new User();
        user.email = email.value();
        user.password = null;
        user.role = role;
        user.provider = provider;
        user.providerId = providerId;
        user.enabled = true;
        user.emailVerified = true;
        return user;
    }

    /**
     * Verifies the user's email address and enables the account.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.enabled = true;
    }

    /**
     * Changes the user's password.
     *
     * @param newPasswordHash the new BCrypt hashed password
     */
    public void changePassword(String newPasswordHash) {
        if (this.provider != AuthProvider.LOCAL) {
            throw new IllegalStateException("Cannot change password for OAuth users");
        }
        this.password = newPasswordHash;
    }

    /**
     * Records a successful login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }

    /**
     * Disables the user account.
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Enables the user account.
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Returns the user's ID as a UserId value object.
     *
     * @return the UserId
     */
    public UserId userId() {
        return UserId.of(this.getId());
    }
}
