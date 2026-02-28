package com.scholara.identity.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when a token has expired.
 */
public class TokenExpiredException extends ScholaraException {

    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }

    public TokenExpiredException(String message) {
        super(ErrorCode.TOKEN_EXPIRED, message);
    }
}
