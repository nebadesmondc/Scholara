package com.scholara.identity.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable identity-related configuration properties.
 */
@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        CookieProperties.class,
        OAuth2Properties.class
})
public class IdentityConfigurationPropertiesRegistrar {
}
