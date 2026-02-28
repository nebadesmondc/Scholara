package com.scholara.identity.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when a token is invalid or malformed.
 */
public class InvalidTokenException extends ScholaraException {

    public InvalidTokenException() {
        super(ErrorCode.TOKEN_INVALID);
    }

    public InvalidTokenException(String message) {
        super(ErrorCode.TOKEN_INVALID, message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(ErrorCode.TOKEN_INVALID, message, cause);
    }
}
