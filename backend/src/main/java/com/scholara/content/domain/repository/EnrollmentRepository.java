package com.scholara.content.domain.repository;

import com.scholara.content.domain.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /**
     * Finds enrollment by course and student.
     *
     * @param courseId  the course ID
     * @param studentId the student ID
     * @return an optional enrollment
     */
    Optional<Enrollment> findByCourseIdAndStudentId(UUID courseId, UUID studentId);

    /**
     * Finds all enrollments for a student.
     *
     * @param studentId the student ID
     * @return a list of enrollments
     */
    List<Enrollment> findByStudentId(UUID studentId);

    /**
     * Checks if a student is enrolled in a course.
     *
     * @param courseId  the course ID
     * @param studentId the student ID
     * @return true if enrolled, false otherwise
     */
    boolean existsByCourseIdAndStudentId(UUID courseId, UUID studentId);
}
