# TaskMaster

A REST API for task and project management built with Java 21 and Spring Boot. Supports JWT authentication, project memberships, task assignment, filtering, search, and task comments.

## Features

- **Authentication:** JWT-based registration and login.
- **Users:** User profiles and management.
- **Projects:** Project creation and membership-based access.
- **Tasks:** Task creation, assignment, and status management.
- **Filtering & Search:** Filter tasks by status and search by title or description.
- **Comments:** Add, view, edit, and delete task comments.
- **API Documentation:** Interactive Swagger UI with Bearer JWT authentication.
- **Database Migrations:** Version-controlled schema management using Flyway.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security
- JWT (JJWT)
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Maven
- Springdoc OpenAPI 3.x
- Docker

## Getting Started

### Prerequisites

- Java 21+
- Maven
- PostgreSQL, or Docker with Docker Compose

### Configuration

Configure the database connection and JWT secret in your environment or `application.properties`.

Example environment variables:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/task_master
SPRING_DATASOURCE_USERNAME=task_user
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_base64_encoded_secret
```

Ensure the variable names match the placeholders configured in your application.

### Run the application

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Alternatively, if Docker Compose is configured:

```bash
docker compose up --build
```

The application runs at `http://localhost:8080` by default.

## Swagger API Documentation

Swagger UI lets you explore endpoints, inspect request and response schemas, and execute API requests from your browser.

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### How to use Swagger

**1. Open Swagger UI**

Start TaskMaster and open the Swagger UI URL above.

**2. Register or log in**

Find the authentication endpoints under the relevant controller tag.

Register a user if necessary, then execute:

```http
POST /api/v1/auth/login
```

Provide the login request body expected by the API. Copy the JWT access token from the response.

**3. Authorize Swagger**

1. Click **Authorize** near the top of Swagger UI.
2. Paste the JWT token into the Bearer authentication field.
3. Click **Authorize**, then close the dialog.

For an HTTP Bearer scheme, normally paste the raw JWT without adding the `Bearer ` prefix.

**4. Execute protected endpoints**

Expand an endpoint, click **Try it out**, supply the required parameters or request body, and click **Execute**.

Swagger will attach the authorization header to protected requests:

```http
Authorization: Bearer <your-jwt>
```

### Authentication and logout

- Swagger's **Authorize** dialog stores the token for authorized requests.
- To clear Swagger's saved token, click **Authorize** and then **Logout**.
- `springdoc.swagger-ui.persistAuthorization=true` preserves authorization across page refreshes.
- Clearing Swagger's token does not invalidate a JWT already issued by the backend. Server-side token invalidation requires a backend revocation mechanism.

Swagger's standard configuration does not automatically extract the JWT from the login response or clear it when an API logout endpoint is called. That behavior requires additional Swagger UI customization.

## API Overview

Base URL: `/api/v1`

| Resource | Operations |
|---|---|
| Authentication | Register and log in |
| Users | Manage user profiles |
| Projects | Create and retrieve projects |
| Tasks | Create, assign, filter, and search tasks |
| Task Comments | Add, list, edit, and delete comments |

### Example task endpoints

Retrieve tasks assigned to the authenticated user:

```http
GET /api/v1/tasks/assigned/me
```

Filter by status:

```http
GET /api/v1/tasks/assigned/me?status=OPEN
```

Search by title or description:

```http
GET /api/v1/tasks/assigned/me?search=login
```

Combine filtering and search:

```http
GET /api/v1/tasks/assigned/me?status=COMPLETED&search=login
```

Exact endpoint availability depends on the current controller implementation. Use Swagger UI as the source of truth for the routes exposed by the running application.

## Database Migrations

Flyway manages database schema changes. Migration files are typically located at:

```text
src/main/resources/db/migration/
```

Create a new versioned migration for each schema change. Avoid modifying migrations that have already been applied to a database.

## Project Goals

TaskMaster is a backend engineering project focused on REST API design, authentication and authorization, relational data modeling, database migrations, and maintainable Spring Boot architecture.
