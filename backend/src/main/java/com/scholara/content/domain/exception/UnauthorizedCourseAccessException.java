package com.scholara.content.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

import java.util.UUID;

/**
 * Exception thrown when a user attempts to modify a course they don't own.
 */
public class UnauthorizedCourseAccessException extends ScholaraException {
    public UnauthorizedCourseAccessException(UUID userId, UUID courseId) {
        super(ErrorCode.UNAUTHORIZED_COURSE_ACCESS,
                String.format("User %s is not authorized to modify course %s", userId, courseId));
    }
}
