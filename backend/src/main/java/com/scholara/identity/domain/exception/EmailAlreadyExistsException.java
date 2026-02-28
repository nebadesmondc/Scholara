package com.scholara.identity.domain.exception;

import com.scholara.shared.domain.Email;
import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

/**
 * Exception thrown when attempting to register with an email that already exists.
 */
public class EmailAlreadyExistsException extends ScholaraException {

    private final String email;

    public EmailAlreadyExistsException(Email email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists: " + email.value());
        this.email = email.value();
    }

    public EmailAlreadyExistsException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
