# TaskMaster

A project and task management REST API built with Java and Spring Boot. TaskMaster supports project collaboration, task assignment, JWT-based authentication, and time-limited project invitations.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Authentication and Security](#authentication-and-security)
- [Projects and Collaboration](#projects-and-collaboration)
- [Task Management](#task-management)
- [JWT Project Invitations](#jwt-project-invitations)
- [API Documentation with Swagger](#api-documentation-with-swagger)
- [Configuration](#configuration)
- [Local Development](#local-development)
- [Security Considerations](#security-considerations)

## Overview

TaskMaster is a backend application designed to manage projects, teams, and tasks through REST APIs.

The application focuses on practical backend engineering concepts:

- RESTful API design
- JWT-based authentication and authorization
- Relational data modeling with PostgreSQL
- Persistence using Spring Data JPA and Hibernate
- Database schema versioning with Flyway
- Role-based project collaboration
- Secure, time-limited project invitations
- API exploration and testing using Swagger UI

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application language |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication and invitation tokens |
| Spring Data JPA / Hibernate | ORM and persistence |
| PostgreSQL | Relational database |
| Flyway | Database schema migrations |
| Maven | Dependency and build management
| Springdoc OpenAPI / Swagger UI | API documentation and testing |

## Architecture

TaskMaster follows a modular backend structure. Each domain is separated into entities, repositories, services, and controllers.

The primary modules include:

- **Users:** Registration, user lookup, and authenticated user access.
- **Projects:** Project creation and ownership.
- **Project memberships:** Association between users and projects, including collaboration roles.
- **Tasks:** Task creation, assignment, status management, and task retrieval.
- **Project invitations:** Secure, time-limited invitation links using signed JWTs.

## Authentication and Security

TaskMaster uses Spring Security with JWT-based authentication.

The intended authentication flow is:

1. A user registers with their credentials.
2. The application stores a password hash rather than a plaintext password.
3. The user authenticates and receives an access token.
4. The client sends the access token in the `Authorization` header.
5. Spring Security validates the token and establishes the authenticated principal.

Example:

```http
Authorization: Bearer <access-token>
```

Protected endpoints use the authenticated identity to determine which user is making the request.

Normal access tokens and invitation tokens have different purposes and must be validated separately.

## Projects and Collaboration

Projects organize tasks and collaboration.

The project domain includes:

- Project creation and ownership.
- Retrieval of projects accessible to a user.
- Project membership management.
- Invitations to join projects.
- Authorization checks for project operations.

The current role model distinguishes project ownership, team leadership, and team membership. Enforce the role rules consistently in service-layer authorization logic.

## Task Management

The task module manages work associated with projects.

The intended capabilities include:

- Creating and retrieving tasks.
- Recording task titles, descriptions, due dates, and status.
- Associating a reporter and an assignee with a task.
- Assigning tasks to users.
- Listing tasks assigned to the authenticated user.
- Filtering tasks by status.
- Searching task titles and descriptions.

Use request DTOs for input validation and response DTOs to avoid exposing persistence entities directly.

## JWT Project Invitations

TaskMaster supports a stateless invitation design using signed JWTs. Invitations do not require a dedicated invitation table.

### Invitation flow

1. An authorized project owner requests an invitation for a user.
2. The backend generates a signed invitation JWT.
3. The backend returns a invitation URL containing the token.
4. The invitee authenticates using their normal account.
5. The backend validates the invitation token and checks that the authenticated user matches the intended invitee.
6. The backend loads the project and creates membership if the user is not already a member.

### Expiration

The default configured lifetime is 48 hours.

After expiration, the invitation token must be rejected. The lifetime is configurable through application properties.

### Acceptance rules

- The token signature and expiration must be valid.
- The token issuer and purpose must be correct.
- The authenticated user must match the invitee in the token.
- The project must still exist.
- Membership creation must be idempotent.
- The database should enforce uniqueness of the project/user membership pair.

### Stateless invitation limitations

Because invitations are not stored in the database:

- Individual invitations cannot be revoked before expiration without introducing additional state.
- A token is not inherently single-use.
- There is no persistent invitation audit history.
- Membership checks must handle repeated and concurrent acceptance requests safely.

These trade-offs are intentional for the initial implementation. If revocation, strict single-use semantics, or invitation audit history becomes necessary, add persistent invitation state.

## API Documentation with Swagger

TaskMaster uses Springdoc OpenAPI to expose interactive API documentation.

When the application is running locally, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Protected endpoints require authentication. In Swagger UI, use the **Authorize** control to provide the normal access token:

```text
Bearer <access-token>
```

The Swagger security scheme should use HTTP Bearer authentication with JWT.

The Swagger UI and OpenAPI documentation routes must be explicitly permitted in Spring Security configuration if they are intended to be publicly accessible.

## Configuration

Configure the database and security secrets through environment variables or local, untracked configuration.

Example `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/taskmaster}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

app.frontend.base-url=${FRONTEND_BASE_URL:http://localhost:3000}
app.invitation.jwt-secret=${INVITATION_JWT_SECRET}
app.invitation.expiration-hours=48
```

These properties are examples; retain the datasource, migration, and security settings that match the actual project configuration.

Generate a dedicated Base64-encoded invitation signing secret with at least 32 random bytes:

```bash
openssl rand -base64 32
```

Set the output as `INVITATION_JWT_SECRET`. Do not reuse the normal access-token signing secret.

## Local Development

### Prerequisites

- A compatible JDK.
- Maven.
- Docker and Docker Compose, if using the project's containerized PostgreSQL setup.
- Environment variables for database credentials and JWT secrets.

### Setup

1. Clone the repository.
2. Configure the database connection and required environment variables.
3. Start PostgreSQL using the project's Docker configuration.
4. Run the application.
5. Allow Flyway to apply pending migrations.
6. Register a test user and authenticate.
7. Create a project and test the project and task APIs.
8. Generate an invitation for another registered user.
9. Authenticate as the invited user and test invitation acceptance.
10. Inspect the resulting membership and verify authorization behavior.

Migrate flyway scripts manually

```bash
mvn flyway:migrate
```

Use the project's existing Maven wrapper if available.

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Security Considerations

- Never store or log plaintext passwords.
- Do not expose password hashes in API responses.
- Keep access-token and invitation-token signing secrets separate.
- Validate token purpose, signature, issuer, expiration, and intended invitee.
- Do not treat an invitation token as a normal authentication token.
- Enforce authorization in service methods, not only in the frontend.
- Enforce a unique constraint on `(project_id, user_id)` in project memberships.
- Avoid logging authorization headers or raw invitation tokens.
- Use HTTPS in deployed environments.
- Keep database credentials and secrets out of source control.
- Test invalid, expired, mismatched, and repeatedly submitted invitation tokens.