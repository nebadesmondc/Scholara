package com.scholara.shared.error;

/**
 * Centralized error codes for the Scholara platform.
 *
 * <p>Each error code has a unique identifier and a default message.
 * These codes are used in API responses for consistent error handling.
 */
public enum ErrorCode {

    // Authentication errors (AUTH_xxx)
    INVALID_CREDENTIALS("AUTH_001", "Invalid email or password"),
    ACCOUNT_DISABLED("AUTH_002", "Account is not verified or has been disabled"),
    ACCOUNT_LOCKED("AUTH_003", "Account has been locked due to too many failed attempts"),
    TOKEN_EXPIRED("AUTH_004", "Token has expired"),
    TOKEN_INVALID("AUTH_005", "Invalid or malformed token"),
    SESSION_EXPIRED("AUTH_006", "Session has expired"),

    // Registration errors (REG_xxx)
    EMAIL_ALREADY_EXISTS("REG_001", "An account with this email already exists"),
    INVALID_OTP("REG_002", "Invalid or expired verification code"),
    PASSWORD_TOO_WEAK("REG_003", "Password does not meet security requirements"),

    // Authorization errors (AUTHZ_xxx)
    ACCESS_DENIED("AUTHZ_001", "Access denied"),
    INSUFFICIENT_PERMISSIONS("AUTHZ_002", "Insufficient permissions for this action"),

    // User errors (USER_xxx)
    USER_NOT_FOUND("USER_001", "User not found"),
    INVALID_PASSWORD("USER_002", "Current password is incorrect"),

    // Validation errors (VAL_xxx)
    VALIDATION_ERROR("VAL_001", "Validation failed"),
    INVALID_REQUEST("VAL_002", "Invalid request format"),

    // Server errors (SRV_xxx)
    INTERNAL_ERROR("SRV_001", "An unexpected error occurred");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
