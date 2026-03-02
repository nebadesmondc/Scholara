package com.scholara.content.api.dto;

import com.scholara.content.domain.model.Subject;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a subject.
 */
public record SubjectResponse(
        UUID id,
        String title,
        Integer orderIndex,
        List<LessonResponse> lessons,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getTitle(),
                subject.getOrderIndex(),
                subject.getLessons().stream().map(LessonResponse::from).toList(),
                subject.getCreatedAt(),
                subject.getUpdatedAt()
        );
    }
}
