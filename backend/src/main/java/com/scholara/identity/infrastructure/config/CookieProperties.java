package com.scholara.identity.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for cookie-based authentication.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "scholara.cookie")
public class CookieProperties {

    /**
     * Whether to set the Secure flag on cookies.
     * Should be true in production (HTTPS only).
     */
    private boolean secure = false;

    /**
     * SameSite attribute for cookies.
     * Options: Strict, Lax, None.
     */
    private String sameSite = "Lax";

    /**
     * Max age for access token cookie in seconds.
     * Default: 15 minutes (900 seconds).
     */
    private long accessTokenMaxAge = 900;

    /**
     * Max age for refresh token cookie in seconds.
     * Default: 7 days (604800 seconds).
     */
    private long refreshTokenMaxAge = 604800;

    /**
     * Domain for the cookies.
     * If not set, cookies are scoped to the current domain.
     */
    private String domain;

}
