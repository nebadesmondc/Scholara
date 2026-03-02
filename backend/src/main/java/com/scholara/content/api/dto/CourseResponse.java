package com.scholara.content.api.dto;

import com.scholara.content.domain.model.Course;
import com.scholara.content.domain.model.CourseStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a course.
 */
public record CourseResponse(
        UUID id,
        String title,
        String description,
        UUID instructorId,
        CourseStatus status,
        List<SubjectResponse> subjects,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructorId(),
                course.getStatus(),
                course.getSubjects().stream().map(SubjectResponse::from).toList(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
