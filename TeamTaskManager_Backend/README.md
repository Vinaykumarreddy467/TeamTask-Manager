# Team Task Manager - Backend

Complete Spring Boot backend for Team Task Manager application with JWT authentication, RBAC, and comprehensive REST API.

## 📋 Quick Start

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 2. Setup Database
```bash
mysql -u root -p
CREATE DATABASE taskmanager;
# Exit and run:
mysql -u root -p taskmanager < database-schema.sql
```

### 3. Configure Database Credentials
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskmanager
    username: root
    password: your_password
```

### 4. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

Backend runs on: **http://localhost:8080/api**

## 📚 Documentation

Read the documentation files in the `docs/` folder:
- `BACKEND_SETUP_GUIDE.md` - Detailed setup instructions
- `BACKEND_SUMMARY.md` - Feature overview
- `BACKEND_FILE_MANIFEST.md` - File checklist
- `TEAM_TASK_MANAGER_GUIDE.md` - Complete project guide

## 🧪 Testing

### Health Check
```bash
curl http://localhost:8080/api/auth/health
```

### Postman
Import `postman-collection.json` to test all 24 endpoints

## 🏗️ Project Structure
```
src/main/java/com/taskmanager/
├── config/          - Spring configuration
├── controller/      - REST controllers (24 endpoints)
├── dto/             - Data transfer objects
├── entity/          - JPA entities
├── exception/       - Exception handling
├── repository/      - Data access layer
├── security/        - JWT authentication
└── service/         - Business logic & RBAC
```

## 📊 Database Schema
- Users
- Projects
- ProjectMembers (Many-to-Many)
- Tasks

## 🔐 Security
- JWT token authentication
- BCrypt password hashing
- Role-based access control (RBAC)
- CORS configuration

## 📡 API Endpoints (24 Total)
- **Authentication**: 3 endpoints
- **Projects**: 5 endpoints
- **Members**: 3 endpoints
- **Tasks**: 8 endpoints
- **Dashboard**: 5 endpoints

## 🚀 Deployment
For Railway deployment, use `application-prod.yml` and set environment variables:
- `DATABASE_URL`
- `JWT_SECRET`
- `DB_USER`
- `DB_PASSWORD`

## 💡 Key Features
✅ JWT Authentication
✅ Role-Based Access Control (RBAC)
✅ Project Management
✅ Task Management
✅ Dashboard Analytics
✅ Error Handling
✅ CORS Support
✅ Production-Ready

---

For detailed setup instructions, see `docs/BACKEND_SETUP_GUIDE.md`
