# Team Task Manager - Full Stack Project

A full-stack task management application built with:
- **Backend**: Spring Boot + Spring Security + JWT + MySQL
- **Frontend**: React (Vite) with beginner-friendly concepts

---

## Project Structure

```
FullProject/
├── TeamTaskManager_Backend/   ← Spring Boot REST API
└── TeamTaskManager_Frontend/  ← React frontend
```

---

## Backend Setup

### Requirements
- Java 17+
- Maven
- MySQL 8

### Steps
1. Create MySQL database:
   ```sql
   CREATE DATABASE taskmanager;
   ```

2. Update database credentials in `application.yml`:
   ```yaml
   datasource:
     username: your_mysql_username
     password: your_mysql_password
   ```

3. Run the backend:
   ```bash
   cd TeamTaskManager_Backend
   mvn spring-boot:run
   ```
   Backend runs at: `http://localhost:8080`

---

## Frontend Setup

### Requirements
- Node.js 18+

### Steps
```bash
cd TeamTaskManager_Frontend
npm install
npm run dev
```
Frontend runs at: `http://localhost:3000`

---

## Bug Fixes Applied (from original backend)

### Bug 1 — Double `/api` in URLs (Critical)
- **Problem**: `application.yml` had `server.servlet.context-path: /api` AND controllers used `@RequestMapping("/api/...")` — causing all URLs to be `/api/api/projects` etc. (404 errors)
- **Fix**: Removed `context-path: /api` from `application.yml`

### Bug 2 — Package-private Exception Classes
- **Problem**: `AccessDeniedException`, `DuplicateEmailException`, `InvalidCredentialsException` were missing `public` keyword — compile errors when referenced from other packages
- **Fix**: Each moved to its own file with `public` access modifier

### Bug 3 — Deprecated JWT API (jjwt 0.12.x)
- **Problem**: `JwtTokenProvider` used `parserBuilder()`, `parseClaimsJws()`, `getBody()` — all removed in jjwt 0.12.x
- **Fix**: Updated to `parser()`, `parseSignedClaims()`, `getPayload()`

---

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/auth/signup | Register |
| POST | /api/auth/login | Login |
| GET | /api/projects | Get all projects |
| POST | /api/projects | Create project |
| PUT | /api/projects/{id} | Update project |
| DELETE | /api/projects/{id} | Delete project |
| GET | /api/projects/{id}/members | Get members |
| POST | /api/projects/{id}/members | Add member |
| DELETE | /api/projects/{id}/members/{memberId} | Remove member |
| GET | /api/projects/{id}/tasks | Get tasks |
| POST | /api/projects/{id}/tasks | Create task |
| PUT | /api/tasks/{id} | Update task |
| PATCH | /api/tasks/{id}/status | Update status |
| PATCH | /api/tasks/{id}/assign | Assign task |
| DELETE | /api/tasks/{id} | Delete task |
| GET | /api/dashboard/overview | Dashboard stats |
| GET | /api/dashboard/overdue-tasks | Overdue tasks |
