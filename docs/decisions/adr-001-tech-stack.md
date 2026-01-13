# ADR-001: Technology Stack Selection

## Status

Accepted

## Date

2026-01-12

## Context

Scholara requires a modern, maintainable technology stack that supports:

- High-concurrency operations (exam submissions)
- Clean architecture with clear boundaries
- Type safety and developer productivity
- Modern language features
- Long-term maintainability

We need to select technologies for both backend and frontend that align with these requirements.

## Decision

### Backend

We will use:

- **Java 25** as the primary language
- **Spring Boot 4+** as the application framework
- **Spring Modulith** for modular architecture
- **Spring Security** for authentication/authorization
- **PostgreSQL** as the primary database
- **Maven** for build management

### Frontend

We will use:

- **Angular 21** as the frontend framework
- **TypeScript** for type-safe development
- **SCSS** for styling

### Infrastructure

We will use:

- **Docker** for containerization
- **Docker Compose** for local development

## Rationale

### Java 25

- Records for immutable data types
- Sealed interfaces for type hierarchies
- Pattern matching for cleaner code
- Virtual threads for efficient I/O handling
- Structured concurrency for safe parallel operations
- Mature ecosystem and tooling

### Spring Boot 4+ / Spring Modulith

- Industry-standard framework with extensive documentation
- Spring Modulith enforces module boundaries at compile time
- Built-in support for event-driven architecture
- Seamless evolution path to microservices
- Strong security integration

### PostgreSQL

- Robust, production-proven database
- Excellent support for complex queries
- Strong consistency guarantees
- Good Spring Data JPA integration

### Angular 21

- Strong typing with TypeScript
- Component-based architecture
- Built-in dependency injection
- Comprehensive routing and forms
- Active development and long-term support

## Consequences

### Positive

- Modern language features improve code quality
- Strong type safety reduces runtime errors
- Mature frameworks provide stability
- Clear upgrade paths for future versions

### Negative

- Java 25 preview features may have API changes
- Team must stay current with framework updates
- Angular has steeper learning curve than alternatives

### Risks

- Spring Boot 4 is newer, may have undiscovered issues

## Related

- [ADR-002: Modulith First](./adr-002-modulith-first.md)
