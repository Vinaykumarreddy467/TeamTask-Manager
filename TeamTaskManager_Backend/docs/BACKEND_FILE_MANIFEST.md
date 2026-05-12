# Backend File Manifest & Integration Guide

## 📋 Complete File List (32 Files)

### **Configuration & Setup Files (2)**
```
pom.xml
application.yml
application-prod.yml (for Railway deployment)
database-schema.sql (for MySQL setup)
```

---

## **Java Source Files by Package**

### **1. Main Application (1 file)**
```
src/main/java/com/taskmanager/TaskManagerApplication.java
```

### **2. Entity Classes (4 files)**
```
src/main/java/com/taskmanager/entity/User.java
src/main/java/com/taskmanager/entity/Project.java
src/main/java/com/taskmanager/entity/ProjectMember.java
src/main/java/com/taskmanager/entity/Task.java
```

### **3. Repository Interfaces (4 files)**
```
src/main/java/com/taskmanager/repository/UserRepository.java
src/main/java/com/taskmanager/repository/ProjectRepository.java
src/main/java/com/taskmanager/repository/ProjectMemberRepository.java
src/main/java/com/taskmanager/repository/TaskRepository.java
```

### **4. Data Transfer Objects (6 files)**
```
src/main/java/com/taskmanager/dto/AuthRequest.java
src/main/java/com/taskmanager/dto/AuthResponse.java
src/main/java/com/taskmanager/dto/ProjectDTO.java
src/main/java/com/taskmanager/dto/ProjectMemberDTO.java
src/main/java/com/taskmanager/dto/TaskDTO.java
src/main/java/com/taskmanager/dto/DashboardDTO.java
```

### **5. Exception Handling (2 files)**
```
src/main/java/com/taskmanager/exception/CustomExceptions.java
src/main/java/com/taskmanager/exception/GlobalExceptionHandler.java
```

### **6. Security Components (2 files)**
```
src/main/java/com/taskmanager/security/JwtTokenProvider.java
src/main/java/com/taskmanager/security/JwtAuthFilter.java
```

### **7. Configuration (2 files)**
```
src/main/java/com/taskmanager/config/SecurityConfig.java
src/main/java/com/taskmanager/config/CorsConfig.java
```

### **8. Service Layer (4 files)**
```
src/main/java/com/taskmanager/service/AuthService.java
src/main/java/com/taskmanager/service/ProjectService.java
src/main/java/com/taskmanager/service/TaskService.java
src/main/java/com/taskmanager/service/DashboardService.java
```

### **9. Controller Layer (4 files)**
```
src/main/java/com/taskmanager/controller/AuthController.java
src/main/java/com/taskmanager/controller/ProjectController.java
src/main/java/com/taskmanager/controller/TaskController.java
src/main/java/com/taskmanager/controller/DashboardController.java
```

---

## 🔄 Integration Checklist

### **Phase 1: Project Setup (30 min)**

- [ ] Create Maven project with structure below:
  ```
  team-task-manager/
  ├── pom.xml
  ├── src/
  │   ├── main/
  │   │   ├── java/com/taskmanager/
  │   │   │   ├── config/
  │   │   │   ├── controller/
  │   │   │   ├── dto/
  │   │   │   ├── entity/
  │   │   │   ├── exception/
  │   │   │   ├── repository/
  │   │   │   ├── security/
  │   │   │   ├── service/
  │   │   │   └── TaskManagerApplication.java
  │   │   └── resources/
  │   │       ├── application.yml
  │   │       └── application-prod.yml
  │   └── test/
  └── target/ (auto-generated)
  ```

- [ ] Copy `pom.xml` to root directory
- [ ] Create all folder structure under `src/main/java/com/taskmanager/`
- [ ] Copy `application.yml` to `src/main/resources/`
- [ ] Copy `application-prod.yml` to `src/main/resources/`
- [ ] Update MySQL credentials in `application.yml`

### **Phase 2: Main Application Class (5 min)**

- [ ] Copy `TaskManagerApplication.java` to `src/main/java/com/taskmanager/`

### **Phase 3: Entity Classes (10 min)**

- [ ] Copy `User.java` → `src/main/java/com/taskmanager/entity/`
- [ ] Copy `Project.java` → `src/main/java/com/taskmanager/entity/`
- [ ] Copy `ProjectMember.java` → `src/main/java/com/taskmanager/entity/`
- [ ] Copy `Task.java` → `src/main/java/com/taskmanager/entity/`

### **Phase 4: Repository Interfaces (5 min)**

- [ ] Copy `UserRepository.java` → `src/main/java/com/taskmanager/repository/`
- [ ] Copy `ProjectRepository.java` → `src/main/java/com/taskmanager/repository/`
- [ ] Copy `ProjectMemberRepository.java` → `src/main/java/com/taskmanager/repository/`
- [ ] Copy `TaskRepository.java` → `src/main/java/com/taskmanager/repository/`

### **Phase 5: DTOs (10 min)**

- [ ] Copy `AuthRequest.java` → `src/main/java/com/taskmanager/dto/`
- [ ] Copy `AuthResponse.java` → `src/main/java/com/taskmanager/dto/`
- [ ] Copy `ProjectDTO.java` → `src/main/java/com/taskmanager/dto/`
- [ ] Copy `ProjectMemberDTO.java` → `src/main/java/com/taskmanager/dto/`
- [ ] Copy `TaskDTO.java` → `src/main/java/com/taskmanager/dto/`
- [ ] Copy `DashboardDTO.java` → `src/main/java/com/taskmanager/dto/`

### **Phase 6: Exception Handling (5 min)**

- [ ] Copy `CustomExceptions.java` → `src/main/java/com/taskmanager/exception/`
- [ ] Copy `GlobalExceptionHandler.java` → `src/main/java/com/taskmanager/exception/`

### **Phase 7: Security Components (10 min)**

- [ ] Copy `JwtTokenProvider.java` → `src/main/java/com/taskmanager/security/`
- [ ] Copy `JwtAuthFilter.java` → `src/main/java/com/taskmanager/security/`

### **Phase 8: Configuration (5 min)**

- [ ] Copy `SecurityConfig.java` → `src/main/java/com/taskmanager/config/`
- [ ] Copy `CorsConfig.java` → `src/main/java/com/taskmanager/config/`

### **Phase 9: Service Layer (15 min)**

- [ ] Copy `AuthService.java` → `src/main/java/com/taskmanager/service/`
- [ ] Copy `ProjectService.java` → `src/main/java/com/taskmanager/service/`
- [ ] Copy `TaskService.java` → `src/main/java/com/taskmanager/service/`
- [ ] Copy `DashboardService.java` → `src/main/java/com/taskmanager/service/`

### **Phase 10: Controller Layer (10 min)**

- [ ] Copy `AuthController.java` → `src/main/java/com/taskmanager/controller/`
- [ ] Copy `ProjectController.java` → `src/main/java/com/taskmanager/controller/`
- [ ] Copy `TaskController.java` → `src/main/java/com/taskmanager/controller/`
- [ ] Copy `DashboardController.java` → `src/main/java/com/taskmanager/controller/`

---

## 🧪 Build & Test Checklist

### **Pre-Build Verification**

- [ ] All 32 Java files copied to correct locations
- [ ] Package structure: `com.taskmanager.*` (check all files)
- [ ] No import errors in IDE
- [ ] MySQL database created: `taskmanager`
- [ ] application.yml has correct DB credentials
- [ ] No duplicate class names

### **Build & Compilation**

- [ ] Run: `mvn clean install`
- [ ] Build successful (no errors)
- [ ] No warnings about missing dependencies
- [ ] Target folder created with JAR file

### **Runtime Verification**

- [ ] Run: `mvn spring-boot:run` or run via IDE
- [ ] Server starts on port 8080
- [ ] No startup errors
- [ ] Logs show: "Started TaskManagerApplication"

### **Functional Testing**

- [ ] GET `http://localhost:8080/api/auth/health` → 200 OK
- [ ] POST `/auth/signup` → 201 Created with token
- [ ] POST `/auth/login` → 200 OK with token
- [ ] POST `/projects` with token → 201 Created
- [ ] GET `/projects` with token → 200 OK with list
- [ ] POST `/projects/{id}/tasks` → 201 Created
- [ ] GET `/dashboard/overview` → 200 OK with metrics
- [ ] Invalid requests return proper error messages

---

## 📦 File Size Reference

| Component | Files | Est. Size |
|-----------|-------|-----------|
| Entities | 4 | ~3 KB |
| DTOs | 6 | ~8 KB |
| Repositories | 4 | ~2 KB |
| Services | 4 | ~20 KB |
| Controllers | 4 | ~15 KB |
| Config/Security | 4 | ~8 KB |
| Exception | 2 | ~4 KB |
| Main App | 1 | ~0.5 KB |
| **Total** | **32** | **~60 KB** |

---

## 🔐 Security Implementation Summary

| Feature | Implementation | Location |
|---------|---|---|
| Password Hashing | BCryptPasswordEncoder | SecurityConfig.java |
| JWT Generation | JwtTokenProvider | security/JwtTokenProvider.java |
| Request Auth | JwtAuthFilter | security/JwtAuthFilter.java |
| CORS | CorsConfig | config/CorsConfig.java |
| Request Access | JwtAuthFilter attribute | Every controller |
| RBAC | Service layer | ProjectService, TaskService |
| Exception Handling | GlobalExceptionHandler | exception/GlobalExceptionHandler.java |

---

## 🌐 API Endpoint Summary

**Total Endpoints: 24**

### Authentication (3)
```
POST   /api/auth/signup
POST   /api/auth/login
GET    /api/auth/health
```

### Projects (5)
```
POST   /api/projects
GET    /api/projects
GET    /api/projects/{id}
PUT    /api/projects/{id}
DELETE /api/projects/{id}
```

### Project Members (2)
```
POST   /api/projects/{id}/members
GET    /api/projects/{id}/members
DELETE /api/projects/{id}/members/{userId}
```

### Tasks (6)
```
POST   /api/projects/{projectId}/tasks
GET    /api/projects/{projectId}/tasks
GET    /api/tasks/{id}
PUT    /api/tasks/{id}
PATCH  /api/tasks/{id}/status
PATCH  /api/tasks/{id}/assign
DELETE /api/tasks/{id}
```

### Dashboard (4)
```
GET    /api/dashboard/overview
GET    /api/dashboard/tasks-by-status
GET    /api/dashboard/tasks-per-user
GET    /api/dashboard/overdue-tasks
```

---

## 🚨 Troubleshooting Quick Links

| Issue | Solution |
|-------|----------|
| Compilation errors | Check package names match `com.taskmanager.*` |
| Database connection | Verify MySQL running, credentials in application.yml |
| Port 8080 in use | Change port in application.yml or kill process |
| JWT errors | Ensure JWT_SECRET is 32+ characters |
| CORS errors | Add frontend URL to CorsConfig.java |
| Import errors | Run `mvn clean install -U` to update dependencies |
| Slow build | Use `mvn clean install -q` for quiet mode |

---

## ✅ Final Integration Steps

1. **Copy all 32 files** to their designated locations
2. **Build with Maven:** `mvn clean install`
3. **Verify no errors** in build output
4. **Run application:** `mvn spring-boot:run`
5. **Test health endpoint:** `curl http://localhost:8080/api/auth/health`
6. **Test signup:** POST with email/password/name
7. **Test with Postman:** Import postman-collection.json
8. **Save token** and test protected endpoints
9. **Verify database** has entries after signup
10. **Ready for frontend!**

---

**Backend Integration Complete! 🎉**

Next: Build the React frontend and connect it to this backend.
