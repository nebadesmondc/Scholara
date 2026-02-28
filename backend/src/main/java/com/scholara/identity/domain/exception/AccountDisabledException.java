package com.scholara.identity.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when attempting to authenticate with a disabled account.
 */
public class AccountDisabledException extends ScholaraException {

    public AccountDisabledException() {
        super(ErrorCode.ACCOUNT_DISABLED);
    }

    public AccountDisabledException(String message) {
        super(ErrorCode.ACCOUNT_DISABLED, message);
    }
}
