# Scholara Development Roadmap

## Purpose

This document outlines the phased development plan for the Scholara education platform, providing a structured approach to implementation.

---

## Phase Overview

| Phase | Focus                        | Status      |
|-------|------------------------------|-------------|
| 0     | Architecture & Planning      | Complete    |
| 1     | Modulith Skeleton            | In Progress |
| 2     | Identity & Security          | Planned     |
| 3     | Academic Content             | Planned     |
| 4     | Assessment Engine            | Planned     |
| 5     | Progress & Analytics         | Planned     |
| 6     | Observability                | Planned     |
| 7     | Microservices Extraction     | Future      |

---

## Phase 0: Architecture & Planning

**Status**: Complete

### Deliverables

- [x] Project vision and goals (README.md)
- [x] Repository structure
- [x] Architecture documentation
- [x] Architecture Decision Records (ADRs)
- [x] Diagram specifications
- [x] Development environment setup

### Acceptance Criteria

- Documentation clearly describes system architecture
- Module boundaries are defined
- Technology choices are documented with rationale

---

## Phase 1: Modulith Skeleton

**Status**: In Progress

### Deliverables

- [x] Backend project structure with Maven
- [x] Spring Boot 4+ application setup
- [x] Spring Modulith configuration
- [x] Empty module packages with boundaries
- [x] Module verification tests
- [x] Frontend Angular workspace
- [x] Core module structure
- [x] Feature module placeholders
- [ ] CI pipeline configuration
- [ ] Development environment validation

### Acceptance Criteria

- `mvn clean verify` passes
- `ModulithStructureTest` validates all boundaries
- Frontend builds without errors
- Docker environment starts successfully

---

## Phase 2: Identity & Security

**Status**: Planned

### Deliverables

- [ ] User entity and repository
- [ ] Registration and login APIs
- [ ] JWT authentication
- [ ] Role-based authorization
- [ ] Password hashing and validation
- [ ] Session management
- [ ] Frontend login/logout flows
- [ ] Route guards implementation

### Acceptance Criteria

- Users can register and login
- JWT tokens are issued and validated
- Role-based access control works
- Security tests pass

---

## Phase 3: Academic Content

**Status**: Planned

### Deliverables

- [ ] Course entity and CRUD operations
- [ ] Subject and Lesson management
- [ ] Content association with instructors
- [ ] Course publishing workflow
- [ ] Student enrollment
- [ ] Frontend course browsing
- [ ] Teacher content management UI

### Acceptance Criteria

- Teachers can create and publish courses
- Students can browse and enroll in courses
- Content module events are published
- Integration tests pass

---

## Phase 4: Assessment Engine

**Status**: Planned

### Deliverables

- [ ] Exam and Question entities
- [ ] Question type hierarchy (sealed interfaces)
- [ ] Exam creation API
- [ ] Submission handling with virtual threads
- [ ] Grading logic
- [ ] Result calculation
- [ ] Frontend exam taking interface
- [ ] Grading UI for teachers

### Acceptance Criteria

- Teachers can create exams
- Students can take exams
- Concurrent submissions handled correctly
- Assessment events published
- Load testing validates concurrency

---

## Phase 5: Progress & Analytics

**Status**: Planned

### Deliverables

- [ ] Learning progress tracking
- [ ] Event consumption from content/assessment
- [ ] Performance aggregation
- [ ] Analytics queries
- [ ] Student dashboard with progress
- [ ] Teacher analytics views

### Acceptance Criteria

- Progress updates on lesson completion
- Performance metrics calculated on exam grading
- Analytics queries perform efficiently
- Event-driven updates work correctly

---

## Phase 6: Observability

**Status**: Planned

### Deliverables

- [ ] Structured logging configuration
- [ ] Metrics endpoints (Micrometer)
- [ ] Health checks
- [ ] Distributed tracing preparation
- [ ] Performance baselines
- [ ] Alerting rules definition

### Acceptance Criteria

- All operations logged with correlation IDs
- Metrics exposed for monitoring
- Health endpoints respond correctly
- Performance baselines documented

---

## Phase 7: Microservices Extraction

**Status**: Future

### Considerations

This phase is intentionally deferred until:

- Clear scaling requirements emerge
- Team size justifies separate services
- Operational maturity supports distributed systems

### Potential Extraction Order

1. Notification module (async, independent scaling)
2. Assessment module (high concurrency requirements)
3. Progress module (analytics workloads)

See [Evolution Roadmap](./docs/architecture/evolution-roadmap.md) for details.

---

## Contributing

Each phase should be independently reviewable. When working on a phase:

1. Create a feature branch for the phase
2. Implement deliverables incrementally
3. Ensure all acceptance criteria are met
4. Update this roadmap with completion status
5. Submit for review

---

## Related Documents

- [System Overview](./docs/architecture/system-overview.md)
- [Evolution Roadmap](./docs/architecture/evolution-roadmap.md)
- [ADR-002: Modulith First](./docs/decisions/adr-002-modulith-first.md)
