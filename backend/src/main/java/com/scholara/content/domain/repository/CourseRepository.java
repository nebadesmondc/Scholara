package com.scholara.content.domain.repository;

import com.scholara.content.domain.model.Course;
import com.scholara.content.domain.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Finds courses by instructor ID.
     *
     * @param instructorId the instructor's UUID
     * @return a list of courses
     */
    List<Course> findByInstructorId(UUID instructorId);

    /**
     * Finds courses by status.
     *
     * @param status the course status
     * @return a list of courses
     */
    List<Course> findByStatus(CourseStatus status);
}
