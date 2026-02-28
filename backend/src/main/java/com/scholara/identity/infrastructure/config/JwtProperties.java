package com.scholara.identity.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for JWT token generation and validation.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "scholara.jwt")
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     * Must be at least 256 bits (32 characters) for HS256.
     */
    private String secret;

    /**
     * Access token validity duration.
     * Default: 15 minutes.
     */
    private Duration accessTokenExpiration = Duration.ofMinutes(15);

    /**
     * Refresh token validity duration.
     * Default: 7 days.
     */
    private Duration refreshTokenExpiration = Duration.ofDays(7);

}
