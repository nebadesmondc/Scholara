# Scholara

Scholara is a modern, modular education and assessment platform designed to demonstrate enterprise-grade architecture patterns and modern Java development practices.

## Overview

This project serves as a reference implementation for building scalable educational platforms using a modular monolith architecture that can evolve into microservices when needed. It prioritizes clean architecture, strict module boundaries, and comprehensive documentation.

### Key Features

- **Modular Monolith Architecture** - Built with Spring Modulith for clear module boundaries
- **Modern Java 25** - Leverages records, sealed interfaces, pattern matching, and virtual threads
- **Role-Based Access** - Supports Student, Teacher, and Admin user roles
- **Event-Driven Communication** - Modules communicate through domain events
- **Evolution-Ready** - Designed for gradual extraction into microservices

## Technology Stack

| Layer          | Technology                                      |
|----------------|------------------------------------------------|
| Backend        | Java 25, Spring Boot 4, Spring Modulith, Spring Security |
| Frontend       | Angular 21, TypeScript, SCSS                   |
| Database       | PostgreSQL 16                                  |
| Build Tools    | Maven (backend), npm (frontend)                |
| Infrastructure | Docker, Docker Compose                         |

## Project Structure

```
scholara/
├── backend/          # Spring Boot modular monolith application
├── frontend/         # Angular single-page application
├── docker/           # Docker Compose and container configurations
├── docs/             # Architecture documentation and ADRs
│   ├── architecture/ # System design documents
│   ├── decisions/    # Architecture Decision Records
│   └── diagrams/     # System diagrams
├── scripts/          # Development and build scripts
└── ROADMAP.md        # Development phases and milestones
```

## Backend Architecture

The backend follows a strict modular monolith pattern with six core modules:

| Module         | Responsibility                                  | Dependencies       |
|----------------|------------------------------------------------|-------------------|
| `identity`     | Authentication, authorization, user management | `shared`          |
| `content`      | Courses, subjects, lessons, learning resources | `shared`, `identity` |
| `assessment`   | Exams, questions, grading, submissions         | `shared`, `identity`, `content` |
| `progress`     | Learning analytics, performance tracking       | `shared`          |
| `notification` | Email and in-app notifications (event-driven)  | `shared`          |
| `shared`       | Value objects, domain events, common types     | none              |

Module boundaries are enforced at compile-time through Spring Modulith annotations and verified by automated tests.

## Frontend Architecture

The Angular application is organized around user roles with a clear separation of concerns:

- **Core Module** - Authentication guards, HTTP interceptors, shared services
- **Feature Modules** - Role-specific functionality (Student, Teacher, Admin)
- **Shared Module** - Reusable components, models, and utilities

## Getting Started

### Prerequisites

- Java 25 or later
- Node.js 20 or later
- Docker and Docker Compose
- Maven 3.9 or later

### Quick Start

1. **Start the database**
   ```bash
   cd docker
   docker-compose up -d
   ```

2. **Run the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Run the frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

The backend will be available at `http://localhost:8091` and the frontend at `http://localhost:4200`.

### Running Tests

```bash
# Backend tests (includes modulith boundary verification)
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

## Documentation

Detailed documentation is available in the `/docs` directory:

- [System Overview](./docs/architecture/system-overview.md) - High-level system design
- [Evolution Roadmap](./docs/architecture/evolution-roadmap.md) - Path from modulith to microservices
- [Architecture Decision Records](./docs/decisions/) - Key technical decisions and rationale

## Development Phases

| Phase | Focus                    | Status      |
|-------|--------------------------|-------------|
| 0     | Architecture & Planning  | Complete    |
| 1     | Modulith Skeleton        | In Progress |
| 2     | Identity & Security      | Planned     |
| 3     | Academic Content         | Planned     |
| 4     | Assessment Engine        | Planned     |
| 5     | Progress & Analytics     | Planned     |
| 6     | Observability            | Planned     |
| 7     | Microservices Extraction | Future      |

See [ROADMAP.md](./ROADMAP.md) for detailed phase descriptions and deliverables.

## License

