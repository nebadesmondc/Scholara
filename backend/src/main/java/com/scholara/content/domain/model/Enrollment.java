package com.scholara.content.domain.model;

import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Enrollment represents a student's subscription to a course.
 */
@Getter
@Setter
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "student_id"})
})
public class Enrollment extends BaseEntity {

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private ZonedDateTime enrolledAt;

    /**
     * Protected constructor for JPA.
     */
    protected Enrollment() {
    }

    /**
     * Creates a new enrollment.
     *
     * @param courseId  the course ID
     * @param studentId the student ID
     * @return a new Enrollment
     */
    public static Enrollment create(UUID courseId, UUID studentId) {
        Enrollment enrollment = new Enrollment();
        enrollment.courseId = courseId;
        enrollment.studentId = studentId;
        enrollment.enrolledAt = ZonedDateTime.now();
        return enrollment;
    }
}
