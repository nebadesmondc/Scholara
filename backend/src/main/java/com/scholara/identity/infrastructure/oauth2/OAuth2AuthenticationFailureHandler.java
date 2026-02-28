package com.scholara.identity.infrastructure.oauth2;

import com.scholara.identity.infrastructure.config.OAuth2Properties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handler for failed OAuth2 authentication attempts.
 *
 * <p>Cleans up OAuth2 authorization request cookies and redirects
 * to the frontend with an error message.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        // Clean up OAuth2 cookies
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // Build error redirect URL
        String targetUrl = UriComponentsBuilder.fromUriString(oAuth2Properties.getFrontendRedirectUri())
                .path("/login")
                .queryParam("error", URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8))
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
