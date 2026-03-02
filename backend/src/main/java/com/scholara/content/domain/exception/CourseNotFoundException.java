package com.scholara.content.domain.exception;

import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;

import java.util.UUID;

/**
 * Exception thrown when a course cannot be found.
 */
public class CourseNotFoundException extends ScholaraException {
    public CourseNotFoundException(UUID courseId) {
        super(ErrorCode.COURSE_NOT_FOUND, "Course not found with ID: " + courseId);
    }
}
