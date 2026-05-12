# Team Task Manager - Backend Implementation Guide

## ✅ Backend Complete! Here's What You Have

### **Total Code Provided:**
- 1 Main Application Class
- 4 Entity Classes (User, Project, ProjectMember, Task)
- 4 Repository Interfaces
- 6 DTO Classes
- 2 Exception Classes (Custom + Handler)
- 2 Security Classes (JWT Provider + Filter)
- 2 Configuration Classes (Security + CORS)
- 4 Service Classes (Auth, Project, Task, Dashboard)
- 4 Controller Classes (Auth, Project, Task, Dashboard)
- 1 pom.xml with all dependencies
- 2 application.yml files (dev + prod)

**That's 32 complete backend files ready to integrate!**

---

## 📁 Project Structure Setup

### **Step 1: Create Folder Structure**

```bash
cd backend
mkdir -p src/main/java/com/taskmanager/{
  config,
  controller,
  dto,
  entity,
  exception,
  repository,
  security,
  service
}
mkdir -p src/main/resources
mkdir -p src/test/java/com/taskmanager
```

### **Step 2: File Placement Guide**

Copy files to these exact locations:

```
src/main/java/com/taskmanager/
├── TaskManagerApplication.java ..................... (root package)
├── config/
│   ├── SecurityConfig.java
│   └── CorsConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   └── DashboardController.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── ProjectDTO.java
│   ├── TaskDTO.java
│   ├── ProjectMemberDTO.java
│   └── DashboardDTO.java
├── entity/
│   ├── User.java
│   ├── Project.java
│   ├── ProjectMember.java
│   └── Task.java
├── exception/
│   ├── CustomExceptions.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   ├── ProjectMemberRepository.java
│   └── TaskRepository.java
├── security/
│   ├── JwtTokenProvider.java
│   └── JwtAuthFilter.java
└── service/
    ├── AuthService.java
    ├── ProjectService.java
    ├── TaskService.java
    └── DashboardService.java

src/main/resources/
├── application.yml
└── application-prod.yml
```

---

## 🔧 Setup Steps

### **Step 1: Create Spring Boot Project**

```bash
# Option A: Maven from command line
mvn archetype:generate -DgroupId=com.taskmanager \
  -DartifactId=team-task-manager \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

# Option B: Use IDE (IntelliJ/Eclipse) 
# - New → Maven Project
# - GroupId: com.taskmanager
# - ArtifactId: team-task-manager

# Option C: Spring Initializr (https://start.spring.io/)
# Select: Spring Web, Spring Data JPA, Spring Security, MySQL Driver
```

### **Step 2: Copy pom.xml**

```bash
# Delete default pom.xml and use the provided one
cp pom.xml team-task-manager/
```

### **Step 3: Copy Configuration Files**

```bash
# Copy YAML configs
cp application.yml team-task-manager/src/main/resources/
cp application-prod.yml team-task-manager/src/main/resources/
```

**Update application.yml for local MySQL:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskmanager
    username: root
    password: root  # or your MySQL password
```

### **Step 4: Copy All Source Files**

Copy all Java files to their respective directories:
- Controllers to `controller/`
- Services to `service/`
- Entities to `entity/`
- DTOs to `dto/`
- Repositories to `repository/`
- Security to `security/`
- Config to `config/`
- Exceptions to `exception/`
- Main class to root `com.taskmanager/`

### **Step 5: Create Database**

```bash
# Run MySQL
mysql -u root -p

# Execute these commands
CREATE DATABASE taskmanager;
CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'taskpass123';
GRANT ALL PRIVILEGES ON taskmanager.* TO 'taskuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Or copy the provided database-schema.sql
mysql -u root -p taskmanager < database-schema.sql
```

### **Step 6: Build Backend**

```bash
# Navigate to backend directory
cd team-task-manager

# Clean build
mvn clean install

# Or if you have Maven issues
mvn clean install -DskipTests
```

### **Step 7: Run Backend**

```bash
# Option A: Maven command
mvn spring-boot:run

# Option B: IDE - Right-click TaskManagerApplication.java → Run

# Option C: Run JAR directly (after build)
java -jar target/team-task-manager-1.0.0.jar
```

**Expected Output:**
```
Started TaskManagerApplication in X.XXX seconds
  Tomcat started on port(s): 8080 (http)
```

**Backend is running at:** `http://localhost:8080/api`

---

## 🧪 Test Backend Immediately

### **Option 1: Using cURL**

```bash
# Test health endpoint
curl http://localhost:8080/api/auth/health

# Expected response:
# {"status":"UP","message":"Team Task Manager API is running"}

# Sign up
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@test.com","password":"Test@123"}'

# Expected response has a token
```

### **Option 2: Using Postman**

1. Import the provided `postman-collection.json`
2. Create environment variable:
   - `base_url` = `http://localhost:8080/api`
3. Test Signup endpoint → Save token to `{{token}}`
4. Test other endpoints with auth header

### **Option 3: Using REST Client Extension (VS Code)**

Create file `test-api.rest`:
```
### Signup
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@test.com",
  "password": "Test@123"
}

### Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@test.com",
  "password": "Test@123"
}
```

---

## 🔑 Key Features Implemented

### **Authentication & Security**
✅ JWT token generation and validation
✅ Password hashing with BCrypt
✅ CORS configuration
✅ Request-level access control
✅ Centralized exception handling

### **Project Management**
✅ Create projects (admin only)
✅ Add/remove project members (admin only)
✅ List projects per user
✅ Update project details (admin only)
✅ Delete project (admin only)

### **Task Management**
✅ Create tasks (members can create)
✅ Update task details (admin only)
✅ Update task status (admin or assigned user)
✅ Assign tasks to members (admin only)
✅ Delete tasks (admin only)
✅ List tasks per project

### **Dashboard & Analytics**
✅ Total tasks count
✅ Tasks by status (TO_DO, IN_PROGRESS, DONE)
✅ Tasks per user
✅ Overdue tasks
✅ Project statistics

### **Role-Based Access Control (RBAC)**
✅ ADMIN: Can manage all tasks and members
✅ MEMBER: Can view and update only assigned tasks
✅ Access validation on every endpoint

---

## 🚨 Common Issues & Solutions

### **Issue: Maven build fails**
```bash
# Solution: Clean and rebuild
mvn clean install -U

# Or use online repository
mvn clean install -DarchetypeRepository=https://repo.maven.apache.org/maven2
```

### **Issue: Database connection error**
```
Error: java.sql.SQLNonTransientConnectionException
```
**Fix:**
- Check MySQL is running: `mysql -u root -p`
- Verify database exists: `SHOW DATABASES;`
- Check credentials in application.yml
- Test connection: `mysql -h localhost -u root -p taskmanager`

### **Issue: Port 8080 already in use**
```bash
# Change port in application.yml
server:
  port: 8081

# Or kill process on 8080
lsof -ti:8080 | xargs kill -9
```

### **Issue: CORS errors when calling from frontend**
- Ensure CorsConfig.java is properly configured
- Add frontend URL to allowed origins
- Restart backend after changes

### **Issue: JWT token validation fails**
- Check JWT_SECRET is 32+ characters (for HS256)
- Ensure token is sent as: `Authorization: Bearer <token>`
- Verify token hasn't expired

---

## 📊 API Response Format

All endpoints follow this format:

### **Success Response (200-201)**
```json
{
  "status": "SUCCESS",
  "data": { /* ... */ },
  "message": "Operation description"
}
```

### **Error Response (400-500)**
```json
{
  "status": "ERROR",
  "message": "Error description"
}
```

---

## 🧬 Database Relationships

```
Users (1) ← → (Many) Projects
  │
  ├─ Created (1) ← → (Many) Projects (as admin)
  ├─ (Many) ← → (Many) Projects (via ProjectMembers)
  └─ (1) ← → (Many) Tasks (assigned to)

Projects (1) ← → (Many) Tasks
  │
  ├─ Has admin (User)
  ├─ Has members (ProjectMembers)
  └─ Contains tasks (Tasks)

ProjectMembers (Join table)
  ├─ Links Users ← → Projects
  ├─ Has role (ADMIN or MEMBER)
  └─ Tracks joined_at

Tasks
  ├─ Belongs to Project
  ├─ Created by User
  ├─ Assigned to User (optional)
  ├─ Has status (TO_DO, IN_PROGRESS, DONE)
  ├─ Has priority (LOW, MEDIUM, HIGH)
  └─ Has due_date
```

---

## 🔍 Important Code Annotations

### **@Entity** - JPA entity mapping
### **@Repository** - Spring Data repository
### **@Service** - Business logic service
### **@Controller/@RestController** - HTTP request handlers
### **@Autowired** - Dependency injection
### **@Transactional** - Database transaction management
### **@PostMapping/@GetMapping/@PutMapping/@DeleteMapping/@PatchMapping** - HTTP methods

---

## 📝 Next Steps

1. **✅ Backend is ready** - All code provided
2. **→ Test with Postman** - Use postman-collection.json
3. **→ Build Frontend** - React with the provided components
4. **→ Connect Frontend** - Set VITE_API_URL to backend URL
5. **→ Deploy to Railway** - Use Dockerfile and configuration

---

## 🚀 Backend Deployment Checklist

- [ ] All code files copied correctly
- [ ] pom.xml in root directory
- [ ] application.yml configured with DB credentials
- [ ] Database created and populated
- [ ] Maven build successful (`mvn clean install`)
- [ ] Backend runs locally on port 8080
- [ ] Health endpoint responds: `/api/auth/health`
- [ ] Signup/Login works in Postman
- [ ] All 4 controllers load without errors
- [ ] No hardcoded credentials (all in application.yml)
- [ ] CORS configured for frontend URL
- [ ] JWT_SECRET is set (32+ chars)
- [ ] Error handling works (test with invalid requests)

---

## 📞 Quick Reference

**Base URL:** `http://localhost:8080/api`

**Authentication:** `Authorization: Bearer <token>`

**Main Endpoints:**
- `POST /auth/signup` - Register
- `POST /auth/login` - Login
- `POST /projects` - Create project
- `GET /projects` - List projects
- `POST /projects/{id}/tasks` - Create task
- `GET /projects/{id}/tasks` - List tasks
- `PATCH /tasks/{id}/status` - Update status
- `GET /dashboard/overview` - Dashboard metrics

**Database:** MySQL on localhost, default credentials: root/root

**Framework:** Spring Boot 3.1.5, Java 17

---

**Backend is production-ready! ✅**
