// taskService.js
// This file handles all API calls related to tasks
// Maps to TaskController.java in the backend

const BASE_URL = 'https://teamtask-manager-6vlw.onrender.com';

function getHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

// GET /api/projects/:projectId/tasks - Get all tasks in a project
export async function getTasksByProject(projectId) {
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}/tasks`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/tasks/:taskId - Get one task by ID
export async function getTaskById(taskId) {
  const response = await fetch(`${BASE_URL}/api/tasks/${taskId}`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// POST /api/projects/:projectId/tasks - Create a task in a project
export async function createTask(projectId, taskData) {
  // taskData = { title, description, priority, dueDate, assignedToId }
  const response = await fetch(`${BASE_URL}/api/projects/${projectId}/tasks`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(taskData)
  });
  const data = await response.json();
  return data;
}

// PUT /api/tasks/:taskId - Update task details
export async function updateTask(taskId, taskData) {
  const response = await fetch(`${BASE_URL}/api/tasks/${taskId}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(taskData)
  });
  const data = await response.json();
  return data;
}

// PATCH /api/tasks/:taskId/status - Update only the status of a task
// Status values: TODO, IN_PROGRESS, DONE
export async function updateTaskStatus(taskId, status) {
  const response = await fetch(`${BASE_URL}/api/tasks/${taskId}/status`, {
    method: 'PATCH',
    headers: getHeaders(),
    body: JSON.stringify({ status })
  });
  const data = await response.json();
  return data;
}

// PATCH /api/tasks/:taskId/assign - Assign task to a user
export async function assignTask(taskId, assignedTo) {
  const response = await fetch(`${BASE_URL}/api/tasks/${taskId}/assign`, {
    method: 'PATCH',
    headers: getHeaders(),
    body: JSON.stringify({ assignedTo })
  });
  const data = await response.json();
  return data;
}

// DELETE /api/tasks/:taskId - Delete a task
export async function deleteTask(taskId) {
  const response = await fetch(`${BASE_URL}/api/tasks/${taskId}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}
