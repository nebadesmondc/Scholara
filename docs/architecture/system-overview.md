# System Overview

## Purpose

This document provides a high-level overview of the Scholara education and assessment platform architecture, describing the key components, their responsibilities, and how they interact.

---

## System Description

Scholara is a modern education platform designed to facilitate learning, assessment, and progress tracking. The system serves three primary user roles:

- **Students**: Access courses, take assessments, track progress
- **Teachers**: Create content, design assessments, monitor student performance
- **Administrators**: Manage users, configure system settings, view analytics

---

## Architecture Style

Scholara follows a **modular monolith** architecture pattern using Spring Modulith. This approach provides:

- Clear module boundaries enforced at compile time
- Event-driven communication between modules
- Single deployable unit with internal structure
- Natural evolution path to microservices

---

## High-Level Components

### Backend (Spring Boot 4+ / Spring Modulith)

The backend is organized into six bounded modules:

| Module         | Responsibility                                    |
|----------------|---------------------------------------------------|
| `identity`     | Authentication, authorization, user management    |
| `content`      | Courses, subjects, lessons, learning resources    |
| `assessment`   | Exams, questions, submissions, grading            |
| `progress`     | Learning analytics, performance tracking          |
| `notification` | Email and in-app notifications                    |
| `shared`       | Common value objects, events, error models        |

### Frontend (Angular 21)

The frontend is a single-page application organized by user role:

| Module    | Responsibility                              |
|-----------|---------------------------------------------|
| `core`    | Layout, guards, interceptors, base services |
| `student` | Student-specific features and views         |
| `teacher` | Teacher-specific features and views         |
| `admin`   | Administrative features and views           |
| `shared`  | Reusable components, models, pipes          |

### Infrastructure

- **Database**: PostgreSQL for persistent storage
- **Containerization**: Docker for local development and deployment
- **Build Tools**: Maven (backend), npm (frontend)

---

## Communication Patterns

### Synchronous

- REST APIs between frontend and backend
- Direct method calls within module boundaries

### Asynchronous

- Domain events for cross-module communication
- Event publication through Spring Modulith Events API

---

## Key Design Decisions

Detailed rationale for architectural decisions can be found in the [Architecture Decision Records](../decisions/)

---

## Related Documents

- [Modulith Boundaries](./modulith-boundaries.md)
- [Domain Model](./domain-model.md)
- [Evolution Roadmap](./evolution-roadmap.md)
