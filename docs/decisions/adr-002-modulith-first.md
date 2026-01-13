# ADR-002: Modulith-First Architecture

## Status

Accepted

## Date

2026-01-12

## Context

When building a new application, teams face a fundamental architectural choice:

1. **Monolith**: Single deployable unit, shared codebase
2. **Microservices**: Distributed services, independent deployment
3. **Modular Monolith**: Single deployment with enforced internal boundaries

Each approach has trade-offs in terms of complexity, operational overhead, and evolution flexibility.

## Decision

We will start with a **modular monolith** architecture using Spring Modulith.

The system will be structured as a single deployable application with:

- Strictly enforced module boundaries
- Event-driven inter-module communication
- Clear API surfaces between modules
- Shared database with logical separation by module

## Rationale

### Why Not Microservices from the Start

- **Distributed systems complexity**: Network failures, eventual consistency, service discovery
- **Operational overhead**: Multiple deployments, monitoring, logging aggregation
- **Premature optimization**: We don't yet know true scaling requirements
- **Team size**: Small teams benefit from shared codebase

### Why Not a Traditional Monolith

- **Boundary erosion**: Without enforcement, modules become tightly coupled
- **Difficult extraction**: Hard to identify service boundaries later
- **Testing complexity**: Changes affect entire system

### Why Modular Monolith

- **Best of both worlds**: Single deployment with clear boundaries
- **Enforced structure**: Spring Modulith validates dependencies at compile time
- **Evolution ready**: Module boundaries make future extraction straightforward
- **Lower complexity**: No distributed system concerns initially
- **Faster development**: Single codebase, simpler debugging

## Implementation

### Module Definition

Each module is defined by:
- Package structure: `com.scholara.<module>`
- `package-info.java` with `@ApplicationModule` annotation
- Explicit `allowedDependencies` declaration

### Communication Patterns

- **Synchronous**: Direct method calls within allowed dependencies
- **Asynchronous**: Domain events for cross-cutting concerns

### Testing

- `ModulithStructureTest` verifies all boundaries at build time
- Module-specific tests for business logic
- Integration tests for cross-module flows

## Consequences

### Positive

- Faster initial development
- Simpler operations and debugging
- Clear path to microservices if needed
- Enforced architectural constraints

### Negative

- Single deployment means full redeploy for any change
- All modules share the same scaling characteristics
- Database is a single point of failure

### Migration Path

When a module needs independent scaling or deployment:
1. Extract module to separate service
2. Replace direct calls with API calls
3. Replace internal events with message broker
4. Migrate module's database tables

## Related

- [System Overview](../architecture/system-overview.md)
- [Modulith Boundaries](../architecture/modulith-boundaries.md)
- [Evolution Roadmap](../architecture/evolution-roadmap.md)
