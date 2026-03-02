package com.scholara.content.api;

import com.scholara.content.api.dto.*;
import com.scholara.content.domain.model.Course;
import com.scholara.content.domain.service.CourseService;
import com.scholara.content.infrastructure.security.SecurityUtils;
import com.scholara.shared.domain.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for course management.
 */
@RestController
@RequestMapping("/v1/courses")
@Validated
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Endpoints for academic content management")
public class CourseController {

    private final CourseService courseService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Get all published courses")
    @ApiResponse(responseCode = "200", description = "List of courses retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getPublishedCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses().stream()
                .map(CourseResponse::from)
                .toList());
    }

    @Operation(summary = "Get a course by ID")
    @ApiResponse(responseCode = "200", description = "Course retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable UUID id) {
        return ResponseEntity.ok(CourseResponse.from(courseService.getCourseById(id)));
    }

    // --- Teacher/Admin Endpoints ---

    @Operation(summary = "Create a new course")
    @ApiResponse(responseCode = "201", description = "Course created successfully")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        UserId instructorId = securityUtils.getCurrentUserId();
        Course course = courseService.createCourse(request.title(), request.description(), instructorId.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.from(course));
    }

    @Operation(summary = "Update an existing course")
    @ApiResponse(responseCode = "200", description = "Course updated successfully")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequest request) {
        UserId instructorId = securityUtils.getCurrentUserId();
        Course course = courseService.updateCourse(id, request.title(), request.description(), instructorId.value());
        return ResponseEntity.ok(CourseResponse.from(course));
    }

    @Operation(summary = "Publish a course")
    @ApiResponse(responseCode = "204", description = "Course published successfully")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> publishCourse(@PathVariable UUID id) {
        UserId instructorId = securityUtils.getCurrentUserId();
        courseService.publishCourse(id, instructorId.value());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a subject to a course")
    @ApiResponse(responseCode = "201", description = "Subject added successfully")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/{id}/subjects")
    public ResponseEntity<SubjectResponse> addSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request) {
        UserId instructorId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubjectResponse.from(courseService.addSubject(id, request.title(), instructorId.value())));
    }

    @Operation(summary = "Add a lesson to a subject")
    @ApiResponse(responseCode = "201", description = "Lesson added successfully")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/{courseId}/subjects/{subjectId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(
            @PathVariable UUID courseId,
            @PathVariable UUID subjectId,
            @Valid @RequestBody LessonRequest request) {
        UserId instructorId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LessonResponse.from(courseService.addLesson(
                        courseId, subjectId, request.title(), request.content(), instructorId.value())));
    }

    @Operation(summary = "Get my courses (as a teacher)")
    @ApiResponse(responseCode = "200", description = "List of courses retrieved successfully")
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/mine")
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        UserId instructorId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId.value()).stream()
                .map(CourseResponse::from)
                .toList());
    }

    @Operation(summary = "Enroll in a course")
    @ApiResponse(responseCode = "201", description = "Enrolled successfully")
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/enroll")
    public ResponseEntity<Void> enroll(@PathVariable UUID id) {
        UserId studentId = securityUtils.getCurrentUserId();
        courseService.enrollStudent(id, studentId.value());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
