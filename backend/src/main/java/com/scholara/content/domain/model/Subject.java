package com.scholara.content.domain.model;

import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject represents a thematic grouping within a course.
 */
@Getter
@Setter
@Entity
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();

    /**
     * Protected constructor for JPA.
     */
    protected Subject() {
    }

    /**
     * Creates a new subject for a course.
     *
     * @param title  the subject title
     * @param course the parent course
     * @return a new Subject
     */
    public static Subject create(String title, Course course) {
        Subject subject = new Subject();
        subject.title = title;
        subject.course = course;
        subject.orderIndex = 0; // Default, will be updated by Course.addSubject
        return subject;
    }

    /**
     * Adds a lesson to the subject.
     *
     * @param lesson the lesson to add
     */
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setSubject(this);
        lesson.setOrderIndex(lessons.size() - 1);
    }

    /**
     * Removes a lesson from the subject.
     *
     * @param lesson the lesson to remove
     */
    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.setSubject(null);
        // Re-index remaining lessons
        for (int i = 0; i < lessons.size(); i++) {
            lessons.get(i).setOrderIndex(i);
        }
    }
}
