package com.scholara.shared.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event published when a student completes a lesson.
 */
public record LessonCompletedEvent(
        UUID lessonId,
        UUID subjectId,
        UUID courseId,
        UUID studentId,
        ZonedDateTime completedAt
) {
}
