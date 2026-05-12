# ⚡ Quick Start Guide - Team Task Manager

**Estimated Time**: 8-12 hours | **Target**: Complete in 1-2 days

---

## 📋 Prerequisites Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] Node.js 16+ installed (`node -v`, `npm -v`)
- [ ] MySQL 8.0+ running locally or access to Railway PostgreSQL
- [ ] Git configured
- [ ] Railway account created (railway.app)
- [ ] Vercel account created (vercel.com) - optional for frontend

---

## 🚀 Phase 1: Project Setup (30 minutes)

### Step 1: Create Project Structure
```bash
# Create root directory
mkdir team-task-manager
cd team-task-manager

# Initialize Git
git init
echo "# Team Task Manager" > README.md
echo ".idea/" > .gitignore
echo ".vscode/" >> .gitignore
echo "*.class" >> .gitignore
echo "target/" >> .gitignore
echo ".DS_Store" >> .gitignore
echo "node_modules/" >> .gitignore
echo ".env" >> .gitignore
echo ".env.local" >> .gitignore

# Create directories
mkdir backend frontend
```

### Step 2: Create Spring Boot Backend
```bash
cd backend

# Option A: Using Spring Boot CLI (if installed)
spring boot new --name=team-task-manager --type=maven --language=java --build=maven

# Option B: Generate from Spring Initializr
# Visit: https://start.spring.io/
# Configure:
# - Project: Maven
# - Language: Java
# - Spring Boot: 3.1.5
# - Packaging: Jar
# - Java: 17
# Dependencies: Spring Web, Spring Data JPA, Spring Security, MySQL Driver, JWT

# Copy the generated pom.xml and src/ structure
# OR manually set up as per the provided pom.xml
```

### Step 3: Create React Frontend
```bash
cd ../frontend

# Create Vite + React project
npm create vite@latest . -- --template react

# Install dependencies
npm install
```

---

## 💾 Phase 2: Backend Development (3-4 hours)

### Step 1: Database Setup

**Option A: Local MySQL**
```bash
# Create database
mysql -u root -p
CREATE DATABASE taskmanager;
CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'taskpass123';
GRANT ALL PRIVILEGES ON taskmanager.* TO 'taskuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Option B: Railway (Skip for now, do in Phase 6)**

### Step 2: Configure Backend

```bash
cd backend

# Copy application.yml to src/main/resources/
mkdir -p src/main/resources
# Paste the application.yml content provided earlier
```

### Step 3: Create Entity Classes

**Create src/main/java/com/taskmanager/entity/User.java**
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
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters (use IDE auto-generate)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

**Repeat for: Project, ProjectMember, Task entities** (follow pattern from guide)

### Step 4: Create Repositories

**src/main/java/com/taskmanager/repository/UserRepository.java**
```java
package com.taskmanager.repository;

import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**Repeat for: ProjectRepository, ProjectMemberRepository, TaskRepository**

### Step 5: Implement Security Components

**src/main/java/com/taskmanager/security/JwtTokenProvider.java** (use provided code from guide)

**src/main/java/com/taskmanager/config/SecurityConfig.java**
```java
package com.taskmanager.config;

import com.taskmanager.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .cors();
        
        return http.build();
    }
}
```

**src/main/java/com/taskmanager/config/JwtAuthFilter.java**
```java
package com.taskmanager.config;

import com.taskmanager.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String email = jwtTokenProvider.getUserEmailFromToken(jwt);
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            // Log but continue - endpoint will reject if auth required
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### Step 6: Create DTOs

**src/main/java/com/taskmanager/dto/AuthRequest.java**
```java
package com.taskmanager.dto;

public class AuthRequest {
    private String name;
    private String email;
    private String password;
    
    public AuthRequest() {}
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

**src/main/java/com/taskmanager/dto/AuthResponse.java**
```java
package com.taskmanager.dto;

public class AuthResponse {
    private String token;
    private String message;
    private Long userId;
    private String name;
    
    public AuthResponse(String token, String message, Long userId, String name) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.name = name;
    }
    
    // Getters
    public String getToken() { return token; }
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
}
```

### Step 7: Create AuthController

**src/main/java/com/taskmanager/controller/AuthController.java**
```java
package com.taskmanager.controller;

import com.taskmanager.dto.AuthRequest;
import com.taskmanager.dto.AuthResponse;
import com.taskmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.signup(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
```

### Step 8: Create AuthService

**Follow the pattern from the comprehensive guide** (create service package with AuthService, ProjectService, TaskService, DashboardService)

### Step 9: Test Backend

```bash
cd backend

# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run via IDE
# The app should start on http://localhost:8080
```

**Test with curl**:
```bash
# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@test.com","password":"Password123"}'

# Should return token
```

---

## 🎨 Phase 3: Frontend Development (2-3 hours)

### Step 1: Setup Tailwind CSS

```bash
cd frontend

# Install Tailwind
npm install -D tailwindcss postcss autoprefixer

# Initialize
npx tailwindcss init -p
```

**tailwind.config.js**:
```javascript
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

**src/index.css**:
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### Step 2: Create .env

**frontend/.env**:
```
VITE_API_URL=http://localhost:8080/api
```

### Step 3: Create API Service

**src/services/api.js**:
```javascript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

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

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Step 4: Create Auth Context

**src/context/AuthContext.jsx**:
```javascript
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

### Step 5: Create useAuth Hook

**src/hooks/useAuth.js**:
```javascript
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

### Step 6: Create Login Component

**src/components/Auth/Login.jsx**:
```jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import api from '../../services/api';

export function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token, userId, name } = response.data;
      
      login(token, { userId, email, name });
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow p-8">
        <h2 className="text-2xl font-bold mb-6">Login</h2>
        {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          
          <div className="mb-6">
            <label className="block text-sm font-medium mb-2">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        
        <p className="mt-4 text-center">
          No account? <Link to="/signup" className="text-blue-600 hover:underline">Sign up</Link>
        </p>
      </div>
    </div>
  );
}
```

### Step 7: Create ProtectedRoute Component

**src/components/Auth/ProtectedRoute.jsx**:
```jsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }
  
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}
```

### Step 8: Create Dashboard Component (Basic)

**src/components/Dashboard/Dashboard.jsx**:
```jsx
import { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import api from '../../services/api';

export function Dashboard() {
  const { user } = useAuth();
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    fetchMetrics();
  }, []);
  
  const fetchMetrics = async () => {
    try {
      const response = await api.get('/dashboard/overview');
      setMetrics(response.data);
    } catch (error) {
      console.error('Failed to fetch metrics:', error);
    } finally {
      setLoading(false);
    }
  };
  
  if (loading) return <div>Loading...</div>;
  
  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-8">Dashboard</h1>
      <p className="mb-4">Welcome, {user?.name}!</p>
      
      {metrics && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-gray-600 text-sm">Total Tasks</h3>
            <p className="text-3xl font-bold">{metrics.totalTasks || 0}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-gray-600 text-sm">In Progress</h3>
            <p className="text-3xl font-bold text-yellow-500">{metrics.inProgress || 0}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-gray-600 text-sm">Completed</h3>
            <p className="text-3xl font-bold text-green-500">{metrics.completed || 0}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-gray-600 text-sm">Overdue</h3>
            <p className="text-3xl font-bold text-red-500">{metrics.overdue || 0}</p>
          </div>
        </div>
      )}
    </div>
  );
}
```

### Step 9: Setup App.jsx and Router

**src/App.jsx**:
```jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Login } from './components/Auth/Login';
import { Signup } from './components/Auth/Signup';
import { ProtectedRoute } from './components/Auth/ProtectedRoute';
import { Dashboard } from './components/Dashboard/Dashboard';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
```

### Step 10: Test Frontend

```bash
npm run dev

# Frontend runs on http://localhost:5173
```

---

## 🚢 Phase 4: Deployment to Railway (2-3 hours)

### Step 1: Prepare Backend for Deployment

```bash
cd backend

# Create Dockerfile
cat > Dockerfile << 'EOF'
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
EOF

# Create .dockerignore
cat > .dockerignore << 'EOF'
target/
.git
.gitignore
README.md
.DS_Store
.env
EOF
```

### Step 2: Create Railway Configuration

```bash
# In root directory, create railway.toml
cat > railway.toml << 'EOF'
[build]
builder = "dockerfile"
dockerfile = "backend/Dockerfile"

[deploy]
startCommand = "java -jar target/team-task-manager-1.0.0.jar --spring.profiles.active=prod"
EOF
```

### Step 3: Deploy to Railway

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link project
railway init

# Create variables
railway variables:set JWT_SECRET=<generate-32-char-string>
railway variables:set DATABASE_URL=<your-db-url>
railway variables:set DB_USER=root
railway variables:set DB_PASSWORD=<password>

# Deploy
railway up
```

### Step 4: Get Backend URL

```bash
# Railway will provide your backend URL
# Example: https://team-task-manager-prod.railway.app/api
# Copy this for frontend configuration
```

### Step 5: Deploy Frontend to Vercel

```bash
cd frontend

# Update .env.production
cat > .env.production << 'EOF'
VITE_API_URL=https://your-railway-backend-url/api
EOF

# Deploy to Vercel
npm run build
vercel --prod
```

---

## ✅ Testing Checklist

### Backend Testing (Postman)

1. **Auth Endpoints**
   - [ ] POST /auth/signup
   - [ ] POST /auth/login
   - [ ] Store token from response

2. **Project Endpoints**
   - [ ] POST /projects (with token)
   - [ ] GET /projects
   - [ ] POST /projects/{id}/members

3. **Task Endpoints**
   - [ ] POST /projects/{id}/tasks
   - [ ] GET /projects/{id}/tasks
   - [ ] PATCH /tasks/{id}/status

4. **Dashboard**
   - [ ] GET /dashboard/overview

### Frontend Testing

- [ ] Can sign up
- [ ] Can log in
- [ ] Dashboard loads
- [ ] Can create projects
- [ ] Can create tasks
- [ ] Can update task status
- [ ] Logout works
- [ ] Protected routes work

---

## 📹 Record Demo Video

1. **Start Recording** (2-5 minutes)
2. **Show each feature**:
   - Signup/Login
   - Create project
   - Add members
   - Create task
   - Update task status
   - View dashboard
3. **Highlight RBAC** (admin vs member)
4. **Show live deployment** URL
5. **Stop and save**

---

## 📋 Final Checklist

- [ ] Backend runs locally without errors
- [ ] Frontend connects to backend
- [ ] All CRUD operations work
- [ ] JWT authentication works
- [ ] RBAC enforced
- [ ] Database migrations run
- [ ] Code pushed to GitHub
- [ ] README complete
- [ ] Environment variables configured
- [ ] Deployment successful
- [ ] Live URLs working
- [ ] Demo video recorded
- [ ] All endpoints tested in Postman
- [ ] Error handling implemented
- [ ] No hardcoded credentials
- [ ] Frontend responsive (mobile-tested)

---

**You got this! 🚀 If stuck, refer to the comprehensive TEAM_TASK_MANAGER_GUIDE.md**
