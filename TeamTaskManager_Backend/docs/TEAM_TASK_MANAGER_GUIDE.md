# Team Task Manager - Full-Stack Implementation Guide

## 📋 Project Overview
Build a collaborative task management application with role-based access control, JWT authentication, and Railway deployment.

**Estimated Timeline**: 8-12 hours | **Recommended**: 2 days

---

## 🏗️ Architecture & Tech Stack

### **Backend**
- **Framework**: Spring Boot 3.x (Java 17+)
- **Database**: MySQL 8.0 (relational structure suits this well)
- **Authentication**: JWT + Spring Security
- **API**: RESTful with proper error handling
- **Build**: Maven

### **Frontend**
- **Framework**: React 18 + Vite
- **Styling**: Tailwind CSS or Material-UI
- **State Management**: React Hooks (useState, useContext, useReducer)
- **HTTP Client**: Axios with interceptors for JWT

### **Deployment**
- **Platform**: Railway
- **Database Hosting**: Railway PostgreSQL or MySQL
- **Backend**: Railway Container
- **Frontend**: Vercel (optional, or Railway)

---

## 🗄️ Database Schema

### **Users Table**
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX (email)
);
```

### **Projects Table**
```sql
CREATE TABLE projects (
    project_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    description TEXT,
    admin_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX (admin_user_id)
);
```

### **Project_Members Table** (Join Table for Users ↔ Projects)
```sql
CREATE TABLE project_members (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('ADMIN', 'MEMBER') DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY (project_id, user_id),
    INDEX (user_id)
);
```

### **Tasks Table**
```sql
CREATE TABLE tasks (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status ENUM('TO_DO', 'IN_PROGRESS', 'DONE') DEFAULT 'TO_DO',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    assigned_to BIGINT,
    due_date DATE,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX (project_id),
    INDEX (assigned_to),
    INDEX (status),
    INDEX (due_date)
);
```

**Relationships**:
```
Users (1) ←→ (Many) Projects (via admin_user_id)
Users (Many) ←→ (Many) Projects (via project_members)
Users (1) ←→ (Many) Tasks (via assigned_to)
Projects (1) ←→ (Many) Tasks
```

---

## 🔌 RESTful API Endpoints

### **Authentication**
```
POST   /api/auth/signup          - Register new user
POST   /api/auth/login           - Login (returns JWT token)
POST   /api/auth/refresh         - Refresh expired token
```

### **Projects**
```
POST   /api/projects             - Create new project (user becomes admin)
GET    /api/projects             - List user's projects
GET    /api/projects/{id}        - Get project details
PUT    /api/projects/{id}        - Update project (admin only)
DELETE /api/projects/{id}        - Delete project (admin only)

POST   /api/projects/{id}/members       - Add member to project (admin only)
DELETE /api/projects/{id}/members/{uid} - Remove member (admin only)
GET    /api/projects/{id}/members       - List project members
```

### **Tasks**
```
POST   /api/projects/{id}/tasks        - Create task
GET    /api/projects/{id}/tasks        - List tasks in project
GET    /api/tasks/{id}                 - Get task details
PUT    /api/tasks/{id}                 - Update task
DELETE /api/tasks/{id}                 - Delete task (admin only)

PATCH  /api/tasks/{id}/status          - Update task status
PATCH  /api/tasks/{id}/assign          - Assign task to user
```

### **Dashboard**
```
GET    /api/dashboard/overview         - Get all dashboard metrics
GET    /api/dashboard/tasks-by-status  - Tasks grouped by status
GET    /api/dashboard/tasks-per-user   - Tasks grouped by assignee
GET    /api/dashboard/overdue-tasks    - List overdue tasks
```

### **Response Format**
```json
// Success
{
  "status": "SUCCESS",
  "data": { /* ... */ },
  "message": "Operation completed"
}

// Error
{
  "status": "ERROR",
  "error": "INVALID_REQUEST",
  "message": "Email already exists",
  "timestamp": "2025-05-11T10:30:00Z"
}
```

---

## 🔐 Authentication & Authorization

### **JWT Token Structure**
```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "user_id",
  "email": "user@example.com",
  "iat": 1234567890,
  "exp": 1234571490,
  "iss": "task-manager"
}
```

### **Role-Based Access Control (RBAC)**
```
ADMIN (in project context):
  ✓ Create/Edit/Delete tasks
  ✓ Assign tasks to any member
  ✓ Manage project members
  ✓ Delete project
  ✓ View all project tasks

MEMBER:
  ✓ View assigned tasks only
  ✓ Update status of assigned tasks
  ✓ View project details
  ✗ Cannot manage members
  ✗ Cannot delete tasks
  ✗ Cannot delete project
```

---

## 📁 Project Structure

```
team-task-manager/
├── backend/
│   ├── src/main/java/com/taskmanager/
│   │   ├── config/
│   │   │   ├── JwtConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── ProjectController.java
│   │   │   ├── TaskController.java
│   │   │   └── DashboardController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── ProjectService.java
│   │   │   ├── TaskService.java
│   │   │   └── DashboardService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── ProjectRepository.java
│   │   │   ├── ProjectMemberRepository.java
│   │   │   └── TaskRepository.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Project.java
│   │   │   ├── ProjectMember.java
│   │   │   └── Task.java
│   │   ├── dto/
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── ProjectDTO.java
│   │   │   ├── TaskDTO.java
│   │   │   └── DashboardDTO.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── CustomExceptions.java
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── TaskManagerApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-prod.yml
│   │   └── db/migration/ (Flyway/Liquibase scripts)
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Auth/
│   │   │   │   ├── Login.jsx
│   │   │   │   ├── Signup.jsx
│   │   │   │   └── ProtectedRoute.jsx
│   │   │   ├── Dashboard/
│   │   │   │   ├── Dashboard.jsx
│   │   │   │   ├── MetricsCard.jsx
│   │   │   │   └── TaskStats.jsx
│   │   │   ├── Projects/
│   │   │   │   ├── ProjectList.jsx
│   │   │   │   ├── ProjectForm.jsx
│   │   │   │   ├── ProjectDetail.jsx
│   │   │   │   └── MemberManager.jsx
│   │   │   ├── Tasks/
│   │   │   │   ├── TaskList.jsx
│   │   │   │   ├── TaskForm.jsx
│   │   │   │   ├── TaskCard.jsx
│   │   │   │   └── TaskFilter.jsx
│   │   │   └── Common/
│   │   │       ├── Navbar.jsx
│   │   │       ├── Sidebar.jsx
│   │   │       └── LoadingSpinner.jsx
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   ├── authService.js
│   │   │   ├── projectService.js
│   │   │   ├── taskService.js
│   │   │   └── dashboardService.js
│   │   ├── context/
│   │   │   ├── AuthContext.jsx
│   │   │   └── ProjectContext.jsx
│   │   ├── hooks/
│   │   │   ├── useAuth.js
│   │   │   └── useApi.js
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── .env.example
│   ├── package.json
│   └── vite.config.js
│
├── README.md
└── .gitignore
```

---

## 🚀 Implementation Roadmap

### **Phase 1: Backend Setup** (2-3 hours)
1. [ ] Create Spring Boot project with Maven
2. [ ] Configure application.yml (DB, JWT secret, CORS)
3. [ ] Set up JPA entities with proper relationships
4. [ ] Create repositories (no custom queries needed initially)
5. [ ] Implement JWT TokenProvider and SecurityConfig
6. [ ] Create AuthService and AuthController (signup/login)
7. [ ] Test auth endpoints with Postman

### **Phase 2: Project & Task Management** (2-3 hours)
1. [ ] Implement ProjectService and ProjectController
2. [ ] Implement TaskService and TaskController
3. [ ] Add RBAC checks in services
4. [ ] Implement project member management
5. [ ] Add proper validation and error handling
6. [ ] Test all endpoints with Postman

### **Phase 3: Dashboard & Analytics** (1-2 hours)
1. [ ] Create DashboardService with aggregation queries
2. [ ] Implement DashboardController endpoints
3. [ ] Add query optimizations (use @Query for efficiency)
4. [ ] Test dashboard endpoints

### **Phase 4: Frontend Setup** (1-2 hours)
1. [ ] Create React + Vite project
2. [ ] Set up Axios with JWT interceptor
3. [ ] Create AuthContext for global auth state
4. [ ] Implement Login and Signup pages
5. [ ] Set up ProtectedRoute component

### **Phase 5: Frontend Features** (2-3 hours)
1. [ ] Create Project CRUD pages
2. [ ] Create Task CRUD pages
3. [ ] Implement Dashboard with metrics
4. [ ] Add filtering and sorting
5. [ ] Add member management UI
6. [ ] Style with Tailwind CSS

### **Phase 6: Deployment & Documentation** (1-2 hours)
1. [ ] Set up Railway PostgreSQL
2. [ ] Deploy backend to Railway
3. [ ] Deploy frontend to Vercel/Railway
4. [ ] Configure environment variables
5. [ ] Write comprehensive README
6. [ ] Record demo video

---

## 💾 Backend Key Code Examples

### **1. User Entity** (Traditional Enterprise Style)
```java
package com.taskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {
    }
    
    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

### **2. JWT Token Provider**
```java
package com.taskmanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateToken(String userEmail, Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .subject(userEmail)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }
    
    public String getUserEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public Long getUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }
    
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
```

### **3. Auth Service**
```java
package com.taskmanager.service;

import com.taskmanager.dto.AuthRequest;
import com.taskmanager.dto.AuthResponse;
import com.taskmanager.entity.User;
import com.taskmanager.exception.DuplicateEmailException;
import com.taskmanager.exception.InvalidCredentialsException;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Transactional
    public AuthResponse signup(AuthRequest authRequest) {
        String email = authRequest.getEmail();
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered: " + email);
        }
        
        User newUser = new User();
        newUser.setName(authRequest.getName());
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(authRequest.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(newUser);
        
        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getUserId());
        
        return new AuthResponse(token, "Signup successful", savedUser.getUserId(), savedUser.getName());
    }
    
    public AuthResponse login(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getUserId());
        
        return new AuthResponse(token, "Login successful", user.getUserId(), user.getName());
    }
}
```

### **4. Project Service (with RBAC)**
```java
package com.taskmanager.service;

import com.taskmanager.dto.ProjectDTO;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.ProjectMember;
import com.taskmanager.entity.User;
import com.taskmanager.exception.AccessDeniedException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.ProjectMemberRepository;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                         ProjectMemberRepository projectMemberRepository,
                         UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setAdminUser(admin);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        
        // Add creator as ADMIN member
        ProjectMember adminMember = new ProjectMember();
        adminMember.setProject(savedProject);
        adminMember.setUser(admin);
        adminMember.setRole("ADMIN");
        adminMember.setJoinedAt(LocalDateTime.now());
        projectMemberRepository.save(adminMember);
        
        return new ProjectDTO(savedProject);
    }
    
    public List<ProjectDTO> getUserProjects(Long userId) {
        List<Project> projects = projectRepository.findByAdminUserUserId(userId);
        
        List<ProjectDTO> dtos = new java.util.ArrayList<>();
        for (Project project : projects) {
            dtos.add(new ProjectDTO(project));
        }
        return dtos;
    }
    
    public ProjectDTO getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        // Verify user has access
        boolean hasMembership = projectMemberRepository
                .existsByProjectProjectIdAndUserUserId(projectId, userId);
        
        if (!project.getAdminUser().getUserId().equals(userId) && !hasMembership) {
            throw new AccessDeniedException("You don't have access to this project");
        }
        
        return new ProjectDTO(project);
    }
    
    @Transactional
    public void addMemberToProject(Long projectId, Long memberId, Long adminUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        // Verify requester is admin
        if (!project.getAdminUser().getUserId().equals(adminUserId)) {
            throw new AccessDeniedException("Only project admin can add members");
        }
        
        User newMember = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if already member
        boolean alreadyMember = projectMemberRepository
                .existsByProjectProjectIdAndUserUserId(projectId, memberId);
        
        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member of this project");
        }
        
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(newMember);
        member.setRole("MEMBER");
        member.setJoinedAt(LocalDateTime.now());
        projectMemberRepository.save(member);
    }
    
    @Transactional
    public void removeMemberFromProject(Long projectId, Long memberId, Long adminUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        if (!project.getAdminUser().getUserId().equals(adminUserId)) {
            throw new AccessDeniedException("Only project admin can remove members");
        }
        
        ProjectMember member = projectMemberRepository
                .findByProjectProjectIdAndUserUserId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in project"));
        
        projectMemberRepository.delete(member);
    }
}
```

---

## 🎨 Frontend Key Components

### **1. Protected Route**
```javascript
// src/components/Auth/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return <div>Loading...</div>;
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
}
```

### **2. API Service with JWT Interceptor**
```javascript
// src/services/api.js
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Request Interceptor - Add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor - Handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### **3. Auth Context**
```javascript
// src/context/AuthContext.jsx
import { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (token && userData) {
      setIsAuthenticated(true);
      setUser(JSON.parse(userData));
    }
    setLoading(false);
  }, []);
  
  const login = (token, userData) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setIsAuthenticated(true);
  };
  
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };
  
  return (
    <AuthContext.Provider value={{ user, isAuthenticated, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
```

---

## 🚢 Railway Deployment Steps

### **1. Prepare Backend**
```bash
# Create application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  
app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000

server:
  port: ${PORT:8080}
  servlet:
    context-path: /api
```

### **2. Create Railway Services**
```bash
# 1. Connect GitHub repo to Railway
# 2. Create PostgreSQL database
# 3. Configure environment variables in Railway dashboard
#    - DATABASE_URL
#    - DB_USER
#    - DB_PASSWORD
#    - JWT_SECRET (generate: openssl rand -hex 32)
#    - RAILWAY_ENVIRONMENT_NAME=production

# 4. Deploy backend
#    - Railway auto-builds and deploys from main branch
#    - Get backend URL from Railway dashboard

# 5. Deploy frontend to Vercel
# 6. Set frontend .env
#    - VITE_API_URL=https://backend-railway-url.railway.app/api
```

### **3. Configure CORS in Backend**
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",
                    System.getenv("FRONTEND_URL")
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 📝 Testing with Postman

### **Create Postman Collection Steps**
1. Create folder structure: Auth, Projects, Tasks, Dashboard
2. Set environment variable: `{{base_url}}` = http://localhost:8080/api
3. Create signup request → save token to environment
4. Use Bearer token in Authorization tab for subsequent requests

**Sample Collection**:
```json
[
  {
    "name": "Signup",
    "request": {
      "method": "POST",
      "url": "{{base_url}}/auth/signup",
      "body": {
        "name": "John Doe",
        "email": "john@example.com",
        "password": "Password@123"
      }
    }
  },
  {
    "name": "Create Project",
    "request": {
      "method": "POST",
      "url": "{{base_url}}/projects",
      "header": {
        "Authorization": "Bearer {{token}}"
      },
      "body": {
        "projectName": "Q2 Planning",
        "description": "Quarterly planning project"
      }
    }
  }
]
```

---

## 📖 README Template

```markdown
# Team Task Manager

A collaborative task management web application built with Spring Boot, React, and MySQL.

## Features

- **User Authentication**: JWT-based secure login/signup
- **Project Management**: Create projects and manage members
- **Task Management**: Create, assign, and track tasks
- **Role-Based Access**: Admin and Member roles with different permissions
- **Dashboard**: Real-time task statistics and metrics
- **Responsive UI**: Mobile-friendly interface

## Tech Stack

**Backend**
- Spring Boot 3.x
- Spring Security + JWT
- Hibernate/JPA
- MySQL 8.0
- Maven

**Frontend**
- React 18
- Vite
- Axios
- Tailwind CSS

**Deployment**
- Railway (Backend & Database)
- Vercel (Frontend)

## Getting Started

### Prerequisites
- Java 17+
- Node.js 16+
- MySQL 8.0
- Maven 3.8+

### Backend Setup

1. Clone and navigate to backend
   ```bash
   cd backend
   ```

2. Configure database
   ```properties
   # src/main/resources/application.yml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/taskmanager
       username: root
       password: your_password
   ```

3. Build and run
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. API runs on: http://localhost:8080/api

### Frontend Setup

1. Navigate to frontend
   ```bash
   cd frontend
   ```

2. Create .env
   ```
   VITE_API_URL=http://localhost:8080/api
   ```

3. Install and run
   ```bash
   npm install
   npm run dev
   ```

4. Frontend runs on: http://localhost:5173

## API Documentation

### Authentication
```
POST /auth/signup
POST /auth/login

Request:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secure_password"
}

Response:
{
  "token": "jwt_token_here",
  "message": "Signup successful",
  "userId": 1,
  "name": "John Doe"
}
```

### Projects
```
POST   /projects           - Create project
GET    /projects           - List user's projects
GET    /projects/{id}      - Get project details
PUT    /projects/{id}      - Update project
DELETE /projects/{id}      - Delete project
```

### Tasks
```
POST   /projects/{id}/tasks - Create task
GET    /projects/{id}/tasks - List tasks
PUT    /tasks/{id}          - Update task
DELETE /tasks/{id}          - Delete task
PATCH  /tasks/{id}/status   - Update status
```

### Dashboard
```
GET /dashboard/overview       - Get metrics
GET /dashboard/tasks-by-status
GET /dashboard/tasks-per-user
```

## Deployment

### Railway

1. **Database**
   - Create PostgreSQL in Railway
   - Note connection details

2. **Backend**
   - Connect GitHub repository
   - Set environment variables:
     - DATABASE_URL
     - JWT_SECRET
     - DATABASE_USER
     - DATABASE_PASSWORD
   - Deploy

3. **Frontend**
   - Deploy to Vercel
   - Set VITE_API_URL to Railway backend URL

## Demo

Watch the 2-5 minute demo explaining:
- User authentication flow
- Project and task management
- Dashboard metrics
- Role-based access control

## Project Timeline

- **Phase 1**: Backend setup (2-3 hours)
- **Phase 2**: Project & Task management (2-3 hours)
- **Phase 3**: Dashboard (1-2 hours)
- **Phase 4**: Frontend setup (1-2 hours)
- **Phase 5**: Frontend features (2-3 hours)
- **Phase 6**: Deployment & docs (1-2 hours)

**Total: 8-12 hours**

## License

MIT
```

---

## 📹 Demo Video Script (2-3 minutes)

```
[0:00-0:30] Introduction
"This is the Team Task Manager application. It's a collaborative platform 
where teams can create projects, manage tasks, and track progress. Let me 
walk you through the key features."

[0:30-1:00] Authentication
"First, user registration and login. Users sign up with their name, email, 
and password. The backend secures everything with JWT tokens."

[1:00-1:30] Projects
"Once logged in, users can create projects. The creator automatically becomes 
an admin. Admins can add team members and manage the project. Members get 
invited and join the team."

[1:30-2:00] Tasks
"Inside each project, we can create tasks with title, description, priority, 
and due date. Tasks can be assigned to team members. Only assigned team 
members can update their own tasks' status."

[2:00-2:30] Dashboard
"The dashboard shows real-time metrics: total tasks, tasks by status, and 
overdue tasks. Admins can see all project tasks; members see only their 
assigned tasks."

[2:30-3:00] Conclusion
"The application demonstrates full-stack development with Spring Boot, React, 
secure authentication, role-based access control, and responsive design."
```

---

## ⚠️ Common Pitfalls to Avoid

1. **CORS Issues**: Configure CORS properly for both localhost and production
2. **Missing JWT in Requests**: Ensure frontend includes Authorization header
3. **Database Connections**: Use environment variables, never hardcode credentials
4. **Deployment Errors**: Test locally before pushing to Railway
5. **Frontend Build**: Ensure frontend builds successfully before deployment
6. **Token Expiration**: Handle token refresh gracefully
7. **Validation**: Validate all inputs on both backend and frontend
8. **Database Schema**: Run migrations before deploying

---

## ✅ Checklist Before Submission

- [ ] Backend builds and runs locally
- [ ] Frontend runs and communicates with backend
- [ ] All CRUD operations work (Create, Read, Update, Delete)
- [ ] Authentication works (signup, login, logout)
- [ ] Role-based access control enforced
- [ ] Dashboard metrics display correctly
- [ ] Postman collection exported with working tests
- [ ] Code is clean and follows conventions
- [ ] README is comprehensive and clear
- [ ] Application deployed on Railway and publicly accessible
- [ ] GitHub repository has all code
- [ ] Demo video recorded (2-5 minutes)
- [ ] .env files are in .gitignore
- [ ] Database migrations are automated
- [ ] Error handling is robust
- [ ] UI is responsive and user-friendly

---

**Good luck with your submission! You've got this! 🚀**
```
