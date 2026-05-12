-- Team Task Manager - Database Schema
-- Run this on MySQL server to create all tables

-- Drop tables if they exist (for fresh setup)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS project_members;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- Create Users Table
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Projects Table
CREATE TABLE projects (
    project_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    description TEXT,
    admin_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_admin_user_id (admin_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Project Members Junction Table
CREATE TABLE project_members (
    member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('ADMIN', 'MEMBER') DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_project_user (project_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Tasks Table
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
    INDEX idx_project_id (project_id),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verify tables created
SHOW TABLES;

-- Sample data (optional - for testing)
-- Uncomment to load sample data

/*
-- Create test users
INSERT INTO users (name, email, password_hash) VALUES
('Admin User', 'admin@test.com', '$2a$10$slYQmyNdGzin7olVchNC6OPST9/PgBkqquzi.Ss69qUiiEW6IKRm'), -- password: Admin123
('John Doe', 'john@test.com', '$2a$10$slYQmyNdGzin7olVchNC6OPST9/PgBkqquzi.Ss69qUiiEW6IKRm'),
('Jane Smith', 'jane@test.com', '$2a$10$slYQmyNdGzin7olVchNC6OPST9/PgBkqquzi.Ss69qUiiEW6IKRm');

-- Create test projects
INSERT INTO projects (project_name, description, admin_user_id) VALUES
('Q2 Planning', 'Quarterly planning and strategy', 1),
('Website Redesign', 'Complete redesign of company website', 1),
('Mobile App', 'Native mobile application development', 2);

-- Add members to projects
INSERT INTO project_members (project_id, user_id, role) VALUES
(1, 1, 'ADMIN'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER'),
(2, 1, 'ADMIN'),
(2, 2, 'MEMBER'),
(3, 2, 'ADMIN'),
(3, 1, 'MEMBER'),
(3, 3, 'MEMBER');

-- Create test tasks
INSERT INTO tasks (project_id, title, description, status, priority, assigned_to, due_date, created_by) VALUES
(1, 'Define Q2 Goals', 'Set KPIs and objectives for Q2', 'IN_PROGRESS', 'HIGH', 1, '2025-06-30', 1),
(1, 'Budget Planning', 'Prepare budget allocations', 'TO_DO', 'HIGH', 2, '2025-06-15', 1),
(2, 'Design Mockups', 'Create UI/UX mockups', 'IN_PROGRESS', 'MEDIUM', 2, '2025-05-30', 1),
(2, 'Developer Setup', 'Setup development environment', 'DONE', 'MEDIUM', 3, '2025-05-20', 1),
(3, 'API Design', 'Design REST API endpoints', 'TO_DO', 'HIGH', 1, '2025-06-10', 2);

-- Verify data
SELECT 'Users' as 'Table';
SELECT * FROM users;

SELECT 'Projects' as 'Table';
SELECT * FROM projects;

SELECT 'Project Members' as 'Table';
SELECT * FROM project_members;

SELECT 'Tasks' as 'Table';
SELECT * FROM tasks;
*/

-- Database info and stats
SELECT 
    CONCAT(COUNT(*), ' users') as users,
    (SELECT COUNT(*) FROM projects) as projects,
    (SELECT COUNT(*) FROM tasks) as tasks
FROM users;

-- Create test user (password: Test@123)
-- To generate bcrypt hash, use online tool or Java BCryptPasswordEncoder
-- For now using pre-generated hash for "Test@123"
INSERT INTO users (name, email, password_hash) VALUES
('Test User', 'test@example.com', '$2a$10$slYQmyNdGzin7olVchNC6OPST9/PgBkqquzi.Ss69qUiiEW6IKRm');

-- Verify insertion
SELECT * FROM users WHERE email = 'test@example.com';
