// dashboardService.js
// This file handles all API calls related to dashboard/statistics
// Maps to DashboardController.java in the backend

const BASE_URL = 'http://localhost:8081';

function getHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

// GET /api/dashboard/overview - Get full dashboard summary
export async function getDashboardOverview() {
  const response = await fetch(`${BASE_URL}/api/dashboard/overview`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/dashboard/tasks-by-status - Get count of tasks grouped by status
export async function getTasksByStatus() {
  const response = await fetch(`${BASE_URL}/api/dashboard/tasks-by-status`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/dashboard/tasks-per-user - Get count of tasks per user
export async function getTasksPerUser() {
  const response = await fetch(`${BASE_URL}/api/dashboard/tasks-per-user`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/dashboard/overdue-tasks - Get list of overdue tasks
export async function getOverdueTasks() {
  const response = await fetch(`${BASE_URL}/api/dashboard/overdue-tasks`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}

// GET /api/dashboard/project/:projectId/statistics - Get stats for one project
export async function getProjectStatistics(projectId) {
  const response = await fetch(`${BASE_URL}/api/dashboard/project/${projectId}/statistics`, {
    method: 'GET',
    headers: getHeaders()
  });
  const data = await response.json();
  return data;
}
