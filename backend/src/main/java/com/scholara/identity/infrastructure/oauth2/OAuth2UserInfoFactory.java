package com.scholara.identity.infrastructure.oauth2;

import com.scholara.shared.domain.AuthProvider;

import java.util.Map;

/**
 * Factory for creating OAuth2UserInfo instances based on the provider.
 */
public final class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
        // Utility class
    }

    /**
     * Creates an OAuth2UserInfo instance for the given provider.
     *
     * @param registrationId the OAuth2 client registration ID (e.g., "google")
     * @param attributes the user attributes from the OAuth2 provider
     * @return the appropriate OAuth2UserInfo implementation
     * @throws OAuth2AuthenticationProcessingException if the provider is not supported
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                    Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);
        }

        throw new OAuth2AuthenticationProcessingException(
                "OAuth2 provider not supported: " + registrationId
        );
    }
}
