package com.scholara.shared.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event published when a course is published.
 */
public record CoursePublishedEvent(
        UUID courseId,
        UUID instructorId,
        String title,
        ZonedDateTime publishedAt
) {
}
