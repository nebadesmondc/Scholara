package com.scholara.identity.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OAuth2 authentication.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "scholara.oauth2")
public class OAuth2Properties {

    /**
     * Frontend URL to redirect to after successful OAuth2 authentication.
     */
    private String frontendRedirectUri = "http://localhost:4200";

    /**
     * List of allowed redirect URIs for OAuth2 callbacks.
     */
    private String[] authorizedRedirectUris = new String[]{
            "http://localhost:4200/oauth2/redirect"
    };

}
