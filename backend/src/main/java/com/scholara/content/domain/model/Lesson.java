package com.scholara.content.domain.model;

import com.scholara.shared.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Lesson represents an individual learning unit.
 */
@Getter
@Setter
@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * Protected constructor for JPA.
     */
    protected Lesson() {
    }

    /**
     * Creates a new lesson for a subject.
     *
     * @param title   the lesson title
     * @param content the lesson content
     * @param subject the parent subject
     * @return a new Lesson
     */
    public static Lesson create(String title, String content, Subject subject) {
        Lesson lesson = new Lesson();
        lesson.title = title;
        lesson.content = content;
        lesson.subject = subject;
        lesson.orderIndex = 0; // Default, will be updated by Subject.addLesson
        return lesson;
    }
}
