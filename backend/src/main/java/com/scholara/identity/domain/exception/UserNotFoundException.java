package com.scholara.identity.domain.exception;

import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.UserId;
import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when a user cannot be found.
 */
public class UserNotFoundException extends ScholaraException {

    public UserNotFoundException(UserId userId) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with ID: " + userId.value());
    }

    public UserNotFoundException(Email email) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email.value());
    }

    public UserNotFoundException(String identifier) {
        super(ErrorCode.USER_NOT_FOUND, "User not found: " + identifier);
    }
}
