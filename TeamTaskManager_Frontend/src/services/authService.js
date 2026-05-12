// authService.js
// This file handles all API calls related to authentication
// Base URL of the backend
//
// NOTE: The backend application.yml has server.servlet.context-path: /api
// AND controllers use @RequestMapping("/api/...") - this causes a double /api issue.
// Fix: Either remove context-path from application.yml (recommended)
//      OR change controller mappings from /api/auth to /auth, /api/projects to /projects, etc.
// This frontend assumes context-path is REMOVED (controllers keep their /api prefix).

const BASE_URL = 'https://teamtask-manager-6vlw.onrender.com';

// Helper to get the token from localStorage
function getToken() {
  return localStorage.getItem('token');
}

// Helper to build headers with Authorization token
function getHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  };
}

// POST /api/auth/signup
export async function signup(name, email, password) {
  const response = await fetch(`${BASE_URL}/api/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password })
  });
  const data = await response.json();
  return data;
}

// POST /api/auth/login
export async function login(email, password) {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  const data = await response.json();
  return data;
}

// GET /api/auth/health
export async function healthCheck() {
  const response = await fetch(`${BASE_URL}/api/auth/health`);
  const data = await response.json();
  return data;
}
