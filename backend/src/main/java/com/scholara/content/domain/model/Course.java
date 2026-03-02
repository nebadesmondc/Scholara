package com.scholara.content.domain.model;

import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Course aggregate root representing an academic course.
 */
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private UUID instructorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Subject> subjects = new ArrayList<>();

    /**
     * Protected constructor for JPA.
     */
    protected Course() {
    }

    /**
     * Creates a new course in DRAFT status.
     *
     * @param title        the course title
     * @param description  the course description
     * @param instructorId the ID of the instructor (teacher)
     * @return a new Course
     */
    public static Course create(String title, String description, UUID instructorId) {
        Course course = new Course();
        course.title = title;
        course.description = description;
        course.instructorId = instructorId;
        course.status = CourseStatus.DRAFT;
        return course;
    }

    /**
     * Publishes the course, making it visible to students.
     */
    public void publish() {
        if (this.status == CourseStatus.PUBLISHED) {
            return;
        }
        this.status = CourseStatus.PUBLISHED;
    }

    /**
     * Archives the course.
     */
    public void archive() {
        this.status = CourseStatus.ARCHIVED;
    }

    /**
     * Adds a subject to the course.
     *
     * @param subject the subject to add
     */
    public void addSubject(Subject subject) {
        subjects.add(subject);
        subject.setCourse(this);
        subject.setOrderIndex(subjects.size() - 1);
    }

    /**
     * Removes a subject from the course.
     *
     * @param subject the subject to remove
     */
    public void removeSubject(Subject subject) {
        subjects.remove(subject);
        subject.setCourse(null);
        // Re-index remaining subjects
        for (int i = 0; i < subjects.size(); i++) {
            subjects.get(i).setOrderIndex(i);
        }
    }
}
