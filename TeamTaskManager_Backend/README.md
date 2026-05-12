# Team Task Manager - Backend

Spring Boot 3.1.5 REST API with PostgreSQL, JWT auth, and role-based access.

## Tech Stack
- Java 17+, Spring Boot 3.1.5
- PostgreSQL, JPA/Hibernate
- JWT authentication (jjwt 0.12.3)
- Maven

## Quick Start
```bash
mvn clean package -DskipTests
java -jar target/team-task-manager-1.0.0.jar
```

## API Endpoints
- `POST /api/auth/signup` - Register
- `POST /api/auth/login` - Login
- `GET/POST /api/projects` - List/Create projects
- `GET/POST/PUT/DELETE /api/projects/{id}` - Project CRUD
- `POST/DELETE /api/projects/{id}/members` - Manage members
- `GET/POST /api/projects/{id}/tasks` - List/Create tasks
- `PUT/PATCH/DELETE /api/tasks/{id}` - Task operations
- `GET /api/dashboard/overview` - Dashboard stats
