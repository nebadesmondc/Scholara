package com.scholara.identity.infrastructure.oauth2;

import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.service.AuthenticationService;
import com.scholara.identity.infrastructure.config.OAuth2Properties;
import com.scholara.identity.infrastructure.security.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler for successful OAuth2 authentication.
 *
 * <p>Sets JWT cookies and redirects to the frontend application.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    private final OAuth2Properties oAuth2Properties;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(
            AuthenticationService authenticationService,
            CookieService cookieService,
            OAuth2Properties oAuth2Properties,
            @Nullable HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        this.authenticationService = authenticationService;
        this.cookieService = cookieService;
        this.oAuth2Properties = oAuth2Properties;
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        ScholaraOAuth2User oAuth2User = (ScholaraOAuth2User) authentication.getPrincipal();
        assert oAuth2User != null;
        User user = oAuth2User.getUser();

        // Generate tokens
        AuthenticationService.AuthenticationResult result =
                authenticationService.authenticateOAuth(user);

        // Set cookies
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createAccessTokenCookie(result.accessToken()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createRefreshTokenCookie(result.refreshToken()).toString());

        // Determine target URL based on role
        String targetUrl = determineTargetUrl(user);

        // Clear any authentication attributes from session
        clearAuthenticationAttributes(request);

        // Clean up OAuth2 authorization request cookies (if using cookie-based storage)
        if (authorizationRequestRepository != null) {
            authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        }

        // Redirect to frontend
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(User user) {
        String frontendUrl = oAuth2Properties.getFrontendRedirectUri();

        return switch (user.getRole()) {
            case ADMIN -> frontendUrl + "/admin/dashboard";
            case TEACHER -> frontendUrl + "/teacher/dashboard";
            case STUDENT -> frontendUrl + "/student/dashboard";
        };
    }
}
