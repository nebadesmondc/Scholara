package com.scholara.identity.infrastructure.oauth2;

import lombok.Getter;

import java.util.Map;

/**
 * Abstract representation of OAuth2 user information.
 *
 * <p>Different OAuth2 providers return user information in different formats.
 * This interface provides a common way to access user attributes.
 */
@Getter
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Returns the unique identifier from the OAuth provider.
     *
     * @return the provider-specific user ID
     */
    public abstract String getId();

    /**
     * Returns the user's display name.
     *
     * @return the name
     */
    public abstract String getName();

    /**
     * Returns the user's email address.
     *
     * @return the email
     */
    public abstract String getEmail();

    /**
     * Returns the URL to the user's profile picture.
     *
     * @return the image URL, or null if not available
     */
    public abstract String getImageUrl();
}
