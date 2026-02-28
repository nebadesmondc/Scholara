package com.scholara.identity.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 */
public class InvalidCredentialsException extends ScholaraException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }
}
