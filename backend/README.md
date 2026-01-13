# Scholara Backend

Spring Boot modular monolith application for the Scholara education platform.

## Overview

The backend is built using Spring Modulith to enforce strict module boundaries while maintaining the simplicity of a single deployable unit. It demonstrates modern Java 25 features and event-driven architecture patterns.

## Technology Stack

- **Java 25** - Modern language features (records, sealed interfaces, pattern matching, virtual threads)
- **Spring Boot 4** - Application framework
- **Spring Modulith 2.0** - Module boundary enforcement and event publication
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Primary database
- **Maven** - Build and dependency management

## Module Structure

```
src/main/java/com/scholara/
├── ScholaraApplication.java    # Application entry point
├── identity/                   # Authentication & user management
│   ├── api/                    # REST controllers and DTOs
│   ├── domain/                 # Entities, repositories, services
│   └── infrastructure/         # External integrations
├── content/                    # Courses, subjects, lessons
│   ├── api/
│   ├── domain/
│   └── infrastructure/
├── assessment/                 # Exams, questions, grading
│   ├── api/
│   ├── domain/
│   └── infrastructure/
├── progress/                   # Learning analytics
│   ├── api/
│   ├── domain/
│   └── infrastructure/
├── notification/               # Notifications (event-driven)
│   ├── api/
│   ├── domain/
│   └── infrastructure/
└── shared/                     # Common types and events
    ├── api/
    ├── domain/
    └── infrastructure/
```

## Module Dependencies

Module boundaries are enforced via `@ApplicationModule` annotations in each module's `package-info.java`:

| Module         | Allowed Dependencies              |
|----------------|----------------------------------|
| `shared`       | none                             |
| `identity`     | `shared`                         |
| `content`      | `shared`, `identity`             |
| `assessment`   | `shared`, `identity`, `content`  |
| `progress`     | `shared`                         |
| `notification` | `shared`                         |

## Prerequisites

- Java 25 or later
- Maven 3.9 or later
- PostgreSQL 16 (or use Docker)

## Building

```bash
# Compile and package
mvn clean package

# Compile and package, skipping tests
mvn clean package -DskipTests

# Full build with verification
mvn clean verify
```

## Running

### Using Docker for PostgreSQL

```bash
# From the project root
cd docker
docker-compose up -d postgres
```

### Starting the Application

```bash
mvn spring-boot:run
```

The application starts on port **8091** by default.

## Testing

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=ModulithStructureTest

# Run tests with coverage
mvn test jacoco:report
```

### Modulith Verification

The `ModulithStructureTest` validates that all module boundaries are respected:

```java
@Test
void verifyModuleStructure() {
    ApplicationModules.of(ScholaraApplication.class).verify();
}
```

This test fails if any module violates its declared dependencies.

## Configuration

Configuration is managed through `src/main/resources/application.yml`:

| Property                    | Default Value                          | Description              |
|-----------------------------|----------------------------------------|--------------------------|
| `server.port`               | `8091`                                 | HTTP server port         |
| `spring.datasource.url`     | `jdbc:postgresql://localhost:5432/scholara` | Database URL        |
| `spring.datasource.username`| `scholara`                             | Database username        |
| `spring.datasource.password`| `scholara`                             | Database password        |

Environment variables can override defaults:
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## API Endpoints

### Actuator

| Endpoint               | Description                    |
|------------------------|--------------------------------|
| `/actuator/health`     | Application health status      |
| `/actuator/info`       | Application information        |
| `/actuator/metrics`    | Application metrics            |
| `/actuator/modulith`   | Module structure information   |

## Development Guidelines

### Adding a New Module

1. Create the module package under `com.scholara`
2. Add `package-info.java` with `@ApplicationModule` annotation
3. Define allowed dependencies
4. Create `api/`, `domain/`, and `infrastructure/` subpackages
5. Run `mvn test` to verify boundaries

### Event Publishing

Modules communicate through domain events:

```java
@Service
public class SomeService {
    private final ApplicationEventPublisher events;

    public void doSomething() {
        // Business logic
        events.publishEvent(new SomethingHappenedEvent(...));
    }
}
```

### Java 25 Features

This project intentionally demonstrates:

- **Records** - For DTOs and value objects
- **Sealed interfaces** - For domain type hierarchies
- **Pattern matching** - In switch expressions
- **Virtual threads** - For I/O-bound operations
- **Structured concurrency** - For parallel operations
