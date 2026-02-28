package com.scholara.shared.error;

/**
 * Base exception class for all Scholara application exceptions.
 *
 * <p>All module-specific exceptions should extend this class to ensure
 * consistent error handling across the platform.
 */
public abstract class ScholaraException extends RuntimeException {

    private final ErrorCode errorCode;

    protected ScholaraException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    protected ScholaraException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected ScholaraException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    protected ScholaraException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
