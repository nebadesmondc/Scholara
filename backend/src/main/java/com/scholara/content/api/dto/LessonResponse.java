package com.scholara.content.api.dto;

import com.scholara.content.domain.model.Lesson;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Response DTO for a lesson.
 */
public record LessonResponse(
        UUID id,
        String title,
        String content,
        Integer orderIndex,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static LessonResponse from(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getOrderIndex(),
                lesson.getCreatedAt(),
                lesson.getUpdatedAt()
        );
    }
}
