package com.scholara.shared.domain;

/**
 * Authentication providers supported by the platform.
 *
 * <p>Determines how a user authenticates with the system.
 */
public enum AuthProvider {

    /**
     * Local authentication using email and password.
     */
    LOCAL,

    /**
     * OAuth2 authentication via Google.
     */
    GOOGLE
}
