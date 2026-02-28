package com.scholara.identity.infrastructure.oauth2;

import com.scholara.identity.infrastructure.security.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Cookie-based repository for storing OAuth2 authorization requests.
 *
 * <p>This implementation stores the OAuth2 authorization request in an HTTP cookie
 * instead of the HTTP session, enabling stateless OAuth2 authentication flows.
 * This is essential when using JWT-based authentication with stateless sessions.
 */
@Slf4j
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.debug("Loading authorization request from cookie");

        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> {
                    log.debug("Found OAuth2 auth request cookie, value length: {}", cookie.getValue().length());
                    try {
                        OAuth2AuthorizationRequest authRequest = CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class);
                        log.debug("Successfully deserialized auth request, state: {}", authRequest.getState());
                        return authRequest;
                    } catch (Exception e) {
                        log.error("Failed to deserialize OAuth2 authorization request cookie: {}", e.getMessage(), e);
                        return null;
                    }
                })
                .orElseGet(() -> {
                    log.warn("OAuth2 authorization request cookie '{}' not found in request",
                        OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
                    return null;
                });
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authorizationRequest == null) {
            log.debug("Removing OAuth2 authorization request cookies");
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        try {
            String serialized = CookieUtils.serialize(authorizationRequest);

            if (serialized.length() > 4000) {
            }

            CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                    serialized, COOKIE_EXPIRE_SECONDS);
            log.info("  Cookie added to response");
        } catch (Exception e) {
            log.error("Failed to serialize OAuth2 authorization request: {}", e.getMessage(), e);
        }

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.hasText(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        log.debug("removeAuthorizationRequest called");
        OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            log.debug("Removing authorization request cookie after loading");
            removeAuthorizationRequestCookies(request, response);
        }
        return authorizationRequest;
    }

    /**
     * Removes OAuth2 authorization request cookies after authentication completes.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
