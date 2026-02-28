package com.scholara.identity.infrastructure.oauth2;

import com.scholara.identity.domain.model.User;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2User implementation that wraps a Scholara User entity.
 */
public class ScholaraOAuth2User implements OAuth2User {

    @Getter
    private final User user;
    private final Map<String, Object> attributes;

    public ScholaraOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().toAuthority()));
    }

    @Override
    @Nonnull
    public String getName() {
        return user.getId().toString();
    }

}
