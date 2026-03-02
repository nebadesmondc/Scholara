package com.scholara.content.domain.service;

import com.scholara.content.domain.exception.CourseNotFoundException;
import com.scholara.content.domain.exception.UnauthorizedCourseAccessException;
import com.scholara.content.domain.model.*;
import com.scholara.content.domain.repository.CourseRepository;
import com.scholara.content.domain.repository.EnrollmentRepository;
import com.scholara.shared.event.CoursePublishedEvent;
import com.scholara.shared.event.StudentEnrolledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing courses, subjects, and lessons.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new course.
     *
     * @param title        the course title
     * @param description  the course description
     * @param instructorId the ID of the instructor (teacher)
     * @return the created course
     */
    public Course createCourse(String title, String description, UUID instructorId) {
        Course course = Course.create(title, description, instructorId);
        return courseRepository.save(course);
    }

    /**
     * Updates an existing course.
     *
     * @param courseId     the ID of the course to update
     * @param title        the new title
     * @param description  the new description
     * @param instructorId the ID of the instructor requesting the update
     * @return the updated course
     */
    public Course updateCourse(UUID courseId, String title, String description, UUID instructorId) {
        Course course = getCourseById(courseId);
        validateInstructor(course, instructorId);

        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    /**
     * Publishes a course and broadcasts a CoursePublishedEvent.
     *
     * @param courseId     the ID of the course to publish
     * @param instructorId the ID of the instructor requesting publication
     */
    public void publishCourse(UUID courseId, UUID instructorId) {
        Course course = getCourseById(courseId);
        validateInstructor(course, instructorId);

        course.publish();
        courseRepository.save(course);

        eventPublisher.publishEvent(new CoursePublishedEvent(
                course.getId(),
                course.getInstructorId(),
                course.getTitle(),
                ZonedDateTime.now()
        ));
    }

    /**
     * Enrolls a student in a course.
     *
     * @param courseId  the ID of the course
     * @param studentId the ID of the student
     * @throws IllegalStateException if course is not published or student already enrolled
     */
    public void enrollStudent(UUID courseId, UUID studentId) {
        Course course = getCourseById(courseId);

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot enroll in a course that is not published");
        }

        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.create(courseId, studentId);
        Enrollment saved = enrollmentRepository.save(enrollment);

        eventPublisher.publishEvent(new StudentEnrolledEvent(
                courseId,
                studentId,
                saved.getEnrolledAt()
        ));

    }

    /**
     * Adds a subject to a course.
     *
     * @param courseId     the ID of the course
     * @param title        the subject title
     * @param instructorId the ID of the instructor
     * @return the added subject
     */
    public Subject addSubject(UUID courseId, String title, UUID instructorId) {
        Course course = getCourseById(courseId);
        validateInstructor(course, instructorId);

        Subject subject = Subject.create(title, course);
        course.addSubject(subject);
        Course savedCourse = courseRepository.save(course);

        // Return the saved subject from the collection to ensure ID is present
        return savedCourse.getSubjects().getLast();
    }

    /**
     * Adds a lesson to a subject.
     *
     * @param courseId     the ID of the parent course
     * @param subjectId    the ID of the subject
     * @param title        the lesson title
     * @param content      the lesson content
     * @param instructorId the ID of the instructor
     * @return the added lesson
     */
    public Lesson addLesson(UUID courseId, UUID subjectId, String title, String content, UUID instructorId) {
        Course course = getCourseById(courseId);
        validateInstructor(course, instructorId);

        Subject subject = course.getSubjects().stream()
                .filter(s -> s.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subject not found in course"));

        Lesson lesson = Lesson.create(title, content, subject);
        subject.addLesson(lesson);
        Course savedCourse = courseRepository.save(course);

        // Find the saved subject then the saved lesson
        Subject savedSubject = savedCourse.getSubjects().stream()
                .filter(s -> s.getId().equals(subjectId))
                .findFirst()
                .orElseThrow();

        return savedSubject.getLessons().getLast();
    }

    /**
     * Retrieves a course by ID.
     *
     * @param courseId the ID of the course
     * @return the course
     */
    @Transactional(readOnly = true)
    public Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    /**
     * Finds all published courses.
     *
     * @return a list of published courses
     */
    @Transactional(readOnly = true)
    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED);
    }

    /**
     * Finds all courses created by a specific instructor.
     *
     * @param instructorId the instructor's ID
     * @return a list of courses
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesByInstructor(UUID instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    private void validateInstructor(Course course, UUID instructorId) {
        if (!course.getInstructorId().equals(instructorId)) {
            throw new UnauthorizedCourseAccessException(instructorId, course.getId());
        }
    }
}
