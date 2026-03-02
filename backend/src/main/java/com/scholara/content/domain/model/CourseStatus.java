package com.scholara.content.domain.model;

/**
 * Status of a course.
 */
public enum CourseStatus {
    /**
     * Initial draft status, only visible to the instructor.
     */
    DRAFT,

    /**
     * Published status, visible to students and open for enrollment.
     */
    PUBLISHED,

    /**
     * Archived status, no longer open for enrollment.
     */
    ARCHIVED
}
