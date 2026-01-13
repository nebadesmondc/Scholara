# Evolution Roadmap

## Purpose

This document outlines the planned architectural evolution of the Scholara platform, from the initial modular monolith to a potential microservices architecture.

---

## Current State: Modular Monolith

The platform begins as a **Spring Modulith** application with:

- Single deployable unit
- Strict module boundaries
- Event-driven inter-module communication
- Shared database with logical separation

### Benefits of Starting Here

1. **Simpler operations**: Single deployment, single database
2. **Easier debugging**: All code in one process
3. **Refactoring flexibility**: Module boundaries can be adjusted
4. **Lower infrastructure costs**: No service mesh, fewer containers

---

## Evolution Phases

### Phase 1: Foundation (Current)

**Focus**: Establish solid modulith architecture

**Deliverables**:
- Project structure with clear boundaries
- Module definitions with enforced dependencies
- Event-driven communication patterns
- Documentation and ADRs

**Success Criteria**:
- `ModulithStructureTest` passes
- All modules have defined boundaries
- No circular dependencies

---

### Phase 2: Core Implementation

**Focus**: Implement core business functionality

**Deliverables**:
- Identity module with authentication/authorization
- Content module with course management
- Assessment module with exam handling
- Basic frontend for all user roles

**Success Criteria**:
- End-to-end user flows working
- Core APIs documented
- Integration tests passing

---

### Phase 3: Advanced Features

**Focus**: Progress tracking and notifications

**Deliverables**:
- Progress module with analytics
- Notification module with multi-channel delivery
- Enhanced frontend dashboards

**Success Criteria**:
- Event consumption working across modules
- Notifications delivered reliably
- Analytics computed correctly

---

### Phase 4: Observability

**Focus**: Production readiness

**Deliverables**:
- Structured logging
- Metrics collection
- Distributed tracing preparation
- Health checks and monitoring

**Success Criteria**:
- All operations observable
- Performance baselines established
- Alerting configured

---

### Phase 5: Microservices Preparation

**Focus**: Prepare for extraction

**Deliverables**:
- API contracts formalized (OpenAPI)
- Event schemas documented
- Database schemas separated by module
- Service boundary analysis

**Success Criteria**:
- Clear extraction candidates identified
- Migration strategy documented
- Risk assessment completed

---

### Phase 6: Selective Extraction

**Focus**: Extract high-value modules

**Candidate Modules for Extraction**:

| Module       | Extraction Priority | Rationale                           |
|--------------|--------------------|------------------------------------|
| notification | High               | Independent, async, scales differently |
| assessment   | Medium             | High concurrency requirements      |
| progress     | Medium             | Analytics may need separate scaling |
| content      | Low                | Core, tightly integrated           |
| identity     | Low                | Critical path, keep stable         |

**Extraction Process**:
1. Create separate service repository
2. Implement API gateway routing
3. Migrate database schema
4. Convert internal events to external messages
5. Update clients to use new endpoints
6. Deprecate monolith module

---

## Decision Triggers for Extraction

Extract a module to a separate service when:

1. **Scale requirements differ**: Module needs independent scaling
2. **Technology requirements**: Module benefits from different tech stack
3. **Team boundaries**: Separate team owns the module
4. **Deployment frequency**: Module changes at different rate
5. **Fault isolation**: Module failures shouldn't affect others

---

## Technical Considerations

### API Gateway
- Route requests to appropriate service
- Handle authentication centrally
- Implement rate limiting

### Service Communication
- Synchronous: REST or gRPC
- Asynchronous: Message broker (RabbitMQ/Kafka)

### Data Management
- Database per service
- Eventual consistency between services
- Saga pattern for distributed transactions

### Observability
- Distributed tracing (OpenTelemetry)
- Centralized logging (ELK stack)
- Metrics aggregation (Prometheus/Grafana)

---

## Anti-Patterns to Avoid

1. **Premature extraction**: Don't extract until pain is real
2. **Distributed monolith**: Avoid tight coupling between services
3. **Shared databases**: Each service owns its data
4. **Synchronous chains**: Minimize cascading synchronous calls

---

## Related Documents

- [System Overview](./system-overview.md)
- [Modulith Boundaries](./modulith-boundaries.md)
- [ADR-002: Modulith First](../decisions/adr-002-modulith-first.md)
