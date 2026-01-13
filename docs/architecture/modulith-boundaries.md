# Modulith Boundaries

## Purpose

This document defines the boundaries and interaction rules between modules in the Scholara backend application, ensuring clean separation of concerns and maintainable architecture.

---

## Module Overview

The backend is divided into six modules, each with explicit allowed dependencies:

```
shared (no dependencies)
    ↑
identity (depends on: shared)
    ↑
content (depends on: shared, identity)
    ↑
assessment (depends on: shared, identity, content)

progress (depends on: shared, identity) ← consumes events from assessment, content
notification (depends on: shared, identity) ← consumes events from all modules
```

---

## Module Definitions

### shared

**Purpose**: Common types used across all modules

**Contains**:
- Value objects (records)
- Domain event definitions
- Error models and exceptions
- Utility types

**Rules**:
- No dependencies on other modules
- No business logic
- All types must be immutable

---

### identity

**Purpose**: User authentication and authorization

**Contains**:
- User entity and repository
- Authentication services
- Authorization logic
- Role and permission management

**API Surface**:
- User lookup by ID
- Authentication endpoints
- Current user context

**Rules**:
- No dependencies on business modules
- Other modules reference users by ID only

---

### content

**Purpose**: Academic content management

**Contains**:
- Course, Subject, Lesson entities
- Content CRUD operations
- Resource management

**API Surface**:
- Course catalog queries
- Content retrieval

**Published Events**:
- `CoursePublished`
- `LessonCompleted`

**Rules**:
- May reference user IDs from identity
- Must not directly call assessment or progress

---

### assessment

**Purpose**: Examinations and grading

**Contains**:
- Exam and Question entities
- Submission handling
- Grading logic

**API Surface**:
- Exam creation and management
- Submission endpoints
- Result queries

**Published Events**:
- `ExamSubmitted`
- `ExamGraded`

**Rules**:
- May reference course/subject IDs from content
- High-concurrency submission handling required
- Must use virtual threads for I/O operations

---

### progress

**Purpose**: Learning analytics

**Contains**:
- Progress tracking entities
- Analytics computation
- Performance aggregation

**API Surface**:
- Progress queries
- Analytics endpoints

**Consumed Events**:
- `LessonCompleted` (from content)
- `ExamGraded` (from assessment)

**Rules**:
- Event-driven only for data updates
- No direct module dependencies beyond identity

---

### notification

**Purpose**: User notifications

**Contains**:
- Notification entities
- Delivery services (email, in-app)
- Preference management

**API Surface**:
- Notification preferences
- Notification history

**Consumed Events**:
- Events from all modules triggering notifications

**Rules**:
- Purely event-driven
- No direct calls from other modules

---

## Interaction Patterns

### Allowed

1. **Direct dependency**: Module A imports public types from Module B (if declared in allowedDependencies)
2. **Event consumption**: Module A listens to events published by Module B
3. **Shared types**: All modules may use types from `shared`

### Prohibited

1. **Circular dependencies**: Module A → Module B → Module A
2. **Undeclared dependencies**: Using types from modules not in allowedDependencies
3. **Direct calls to event-driven modules**: Calling notification or progress directly

---

## Enforcement

Module boundaries are enforced through:

1. **Spring Modulith**: `@ApplicationModule` annotations with `allowedDependencies`
2. **Architecture Tests**: `ModulithStructureTest` verifies boundaries at build time
3. **Package Structure**: Each module has its own package hierarchy

---

## Related Documents

- [System Overview](./system-overview.md)
- [Domain Model](./domain-model.md)
