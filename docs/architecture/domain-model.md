# Domain Model

## Purpose

This document describes the core domain entities, their relationships, and the business rules governing the Scholara education platform.

---

## Domain Overview

Scholara operates in the **education and assessment** domain, focusing on:

- User identity and access control
- Academic content organization
- Assessment and evaluation
- Learning progress tracking

---

## Core Entities by Module

### Identity Module

#### User
The central identity entity representing all platform users.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| email         | String        | Unique email address           |
| passwordHash  | String        | Hashed password                |
| role          | Role          | User's primary role            |
| status        | AccountStatus | Active, suspended, etc.        |
| createdAt     | Instant       | Account creation timestamp     |

#### Role (Enumeration)
- `STUDENT`
- `TEACHER`
- `ADMIN`

---

### Content Module

#### Course
Top-level container for educational content.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| title         | String        | Course title                   |
| description   | String        | Course description             |
| instructorId  | UUID          | Reference to User (teacher)    |
| status        | CourseStatus  | Draft, published, archived     |
| subjects      | List<Subject> | Contained subjects             |

#### Subject
Thematic grouping within a course.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| title         | String        | Subject title                  |
| courseId      | UUID          | Parent course reference        |
| lessons       | List<Lesson>  | Contained lessons              |
| orderIndex    | Integer       | Display order within course    |

#### Lesson
Individual learning unit.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| title         | String        | Lesson title                   |
| content       | String        | Lesson content (markdown)      |
| subjectId     | UUID          | Parent subject reference       |
| orderIndex    | Integer       | Display order within subject   |

---

### Assessment Module

#### Exam
Assessment instrument for evaluating student knowledge.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| title         | String        | Exam title                     |
| courseId      | UUID          | Associated course              |
| creatorId     | UUID          | Reference to User (teacher)    |
| duration      | Duration      | Time limit for completion      |
| questions     | List<Question>| Exam questions                 |
| status        | ExamStatus    | Draft, active, closed          |

#### Question
Individual assessment item.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| text          | String        | Question text                  |
| type          | QuestionType  | Multiple choice, essay, etc.   |
| points        | Integer       | Maximum points                 |
| options       | List<Option>  | Answer options (if applicable) |

#### Submission
Student's exam submission.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| examId        | UUID          | Reference to Exam              |
| studentId     | UUID          | Reference to User (student)    |
| answers       | List<Answer>  | Student's answers              |
| submittedAt   | Instant       | Submission timestamp           |
| score         | Integer       | Calculated score (nullable)    |
| gradedAt      | Instant       | Grading timestamp (nullable)   |

---

### Progress Module

#### LearningProgress
Tracks student progress through content.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| studentId     | UUID          | Reference to User              |
| courseId      | UUID          | Reference to Course            |
| completedLessons | Set<UUID>  | Completed lesson IDs           |
| lastAccessedAt | Instant      | Last activity timestamp        |

#### PerformanceMetric
Aggregated performance data.

| Attribute     | Type          | Description                    |
|---------------|---------------|--------------------------------|
| id            | UUID          | Unique identifier              |
| studentId     | UUID          | Reference to User              |
| courseId      | UUID          | Reference to Course            |
| averageScore  | BigDecimal    | Average exam score             |
| totalExams    | Integer       | Number of exams taken          |

---

### Notification Module

#### Notification
User notification entity.

| Attribute     | Type               | Description                 |
|---------------|--------------------|-----------------------------|
| id            | UUID               | Unique identifier           |
| userId        | UUID               | Target user                 |
| type          | NotificationType   | Email, in-app               |
| title         | String             | Notification title          |
| message       | String             | Notification content        |
| read          | Boolean            | Read status                 |
| createdAt     | Instant            | Creation timestamp          |

---

## Domain Events

### Content Events
- `CoursePublished(courseId, publishedAt)`
- `LessonCompleted(lessonId, studentId, completedAt)`

### Assessment Events
- `ExamSubmitted(examId, studentId, submittedAt)`
- `ExamGraded(examId, studentId, score, gradedAt)`

---

## Aggregate Boundaries

| Aggregate Root | Contains                          |
|----------------|-----------------------------------|
| User           | User (single entity)              |
| Course         | Course → Subject → Lesson         |
| Exam           | Exam → Question → Option          |
| Submission     | Submission → Answer               |
| LearningProgress | LearningProgress (single entity)|
| Notification   | Notification (single entity)      |

---

## Related Documents

- [System Overview](./system-overview.md)
- [Modulith Boundaries](./modulith-boundaries.md)
