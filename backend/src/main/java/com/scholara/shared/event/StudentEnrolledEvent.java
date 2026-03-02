package com.scholara.shared.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event published when a student enrolls in a course.
 */
public record StudentEnrolledEvent(
        UUID courseId,
        UUID studentId,
        ZonedDateTime enrolledAt
) {
}
