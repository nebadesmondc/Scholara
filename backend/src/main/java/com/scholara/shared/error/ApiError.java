package com.scholara.shared.error;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response structure.
 *
 * <p>All API errors are returned in this format for consistent client handling.
 */
public record ApiError(
        String code,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> details
) {
    /**
     * Creates an ApiError from an ErrorCode.
     *
     * @param errorCode the error code enum
     * @param path the request path
     * @return a new ApiError instance
     */
    public static ApiError of(ErrorCode errorCode, String path) {
        return new ApiError(
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                Instant.now(),
                path,
                Map.of()
        );
    }

    /**
     * Creates an ApiError from an ErrorCode with a custom message.
     *
     * @param errorCode the error code enum
     * @param message custom error message
     * @param path the request path
     * @return a new ApiError instance
     */
    public static ApiError of(ErrorCode errorCode, String message, String path) {
        return new ApiError(
                errorCode.getCode(),
                message,
                Instant.now(),
                path,
                Map.of()
        );
    }

    /**
     * Creates an ApiError with validation details.
     *
     * @param errorCode the error code enum
     * @param path the request path
     * @param details field-level validation errors
     * @return a new ApiError instance
     */
    public static ApiError withDetails(ErrorCode errorCode, String path, Map<String, String> details) {
        return new ApiError(
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                Instant.now(),
                path,
                details
        );
    }
}
