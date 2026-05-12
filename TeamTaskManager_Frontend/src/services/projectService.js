// projectService.js
// This file handles all API calls related to projects
// Maps to ProjectController.java in the backend

const BASE_URL = 'https://teamtask-manager-6vlw.onrender.com';

function getHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

// GET /api/projects - Get all projects for logged-in user
export async function getAllProjects() {
  const response = await fetch(`${BASE_URL}/api/projects`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/projects/:id - Get one project by ID
export async function getProjectById(projectId) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// POST /api/projects - Create a new project
export async function createProject(projectName, description) {
  const response = await fetch(`${BASE_URL}/api/projects`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ projectName, description })
  });
  const data = await response.json();
  return data;
}

// PUT /api/projects/:id - Update a project
export async function updateProject(projectId, projectName, description) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify({ projectName, description })
  });
  const data = await response.json();
  return data;
}

// DELETE /api/projects/:id - Delete a project
export async function deleteProject(projectId) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/projects/:id/members - Get all members of a project
export async function getProjectMembers(projectId) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}/members`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// POST /api/projects/:id/members - Add a member to a project (by ID or email)
export async function addMember(projectId, value, isEmail = false) {
  const body = isEmail ? { email: value } : { memberId: value };
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}/members`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(body)
  });
  const data = await response.json();
  return data;
}

// DELETE /api/projects/:id/members/:memberId - Remove a member from project
export async function removeMember(projectId, memberId) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}/members/${memberId}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}
