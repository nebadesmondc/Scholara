package com.scholara.identity.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when an OTP verification fails.
 */
public class InvalidOtpException extends ScholaraException {

    public InvalidOtpException() {
        super(ErrorCode.INVALID_OTP);
    }

    public InvalidOtpException(String message) {
        super(ErrorCode.INVALID_OTP, message);
    }
}
