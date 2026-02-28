package com.scholara.identity.infrastructure.security;

import com.scholara.identity.domain.model.User;
import com.scholara.shared.domain.Role;
import com.scholara.shared.domain.UserId;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetails implementation that wraps a User entity.
 *
 * <p>This is the principal object stored in the SecurityContext
 * after successful authentication.
 *
 * @param user -- GETTER --
 *             Returns the underlying User entity.
 */
public record ScholaraPrincipal(User user) implements UserDetails {

    @Override
    @Nonnull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().toAuthority()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @Nonnull
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * Returns the user's ID.
     *
     * @return the UserId
     */
    public UserId getUserId() {
        return user.userId();
    }

    /**
     * Returns the user's role.
     *
     * @return the Role
     */
    public Role getRole() {
        return user.getRole();
    }
}
