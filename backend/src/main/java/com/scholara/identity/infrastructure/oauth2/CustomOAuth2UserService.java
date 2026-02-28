package com.scholara.identity.infrastructure.oauth2;

import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.service.UserService;
import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom OAuth2 user service that loads or creates users from OAuth2 providers.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return processOAuth2User(registrationId, oAuth2User);
    }

    private OAuth2User processOAuth2User(String registrationId, OAuth2User oAuth2User) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oAuth2User.getAttributes()
        );

        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email not found from OAuth2 provider"
            );
        }

        try {
            Email email = Email.of(userInfo.getEmail());
            AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

            User user = userService.findOrCreateOAuthUser(
                    email,
                    provider,
                    userInfo.getId(),
                    Role.STUDENT
            );

            return new ScholaraOAuth2User(user, oAuth2User.getAttributes());
        } catch (Exception e) {
            throw new OAuth2AuthenticationProcessingException(
                    "Failed to process OAuth2 user: " + e.getMessage(), e
            );
        }
    }
}
