// TasksPage.jsx
// Shows tasks for a specific project
// Lets user create, update, delete tasks and change task status

import { useState, useEffect } from 'react';
import {
  getTasksByProject,
  createTask,
  updateTask,
  updateTaskStatus,
  assignTask,
  deleteTask
} from '../services/taskService.js';
import { getProjectMembers } from '../services/projectService.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function TasksPage({ projectId, projectName, onBack }) {
  const [tasks, setTasks] = useState([]);
  const [members, setMembers] = useState([]); // project members for assigning tasks
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Create task form
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newTask, setNewTask] = useState({ title: '', description: '', priority: 'MEDIUM', dueDate: '', assignedToId: '' });
  const [createError, setCreateError] = useState('');

  // Edit task
  const [editingTask, setEditingTask] = useState(null);
  const [editTask, setEditTask] = useState({});

  const { user } = useAuth();

  // Load tasks and project members when page opens
  useEffect(() => {
    fetchTasks();
    fetchMembers();
  }, [projectId]);

  async function fetchTasks() {
    setLoading(true);
    setError('');
    try {
      const result = await getTasksByProject(projectId);
      if (result.status === 'SUCCESS') {
        setTasks(result.data || []);
      } else {
        setError('Could not load tasks.');
      }
    } catch (err) {
      setError('Could not connect to server.');
    }
    setLoading(false);
  }

  async function fetchMembers() {
    try {
      const result = await getProjectMembers(projectId);
      if (result.status === 'SUCCESS') {
        setMembers(result.data || []);
      }
    } catch (err) {
      // silently fail - members list is optional
    }
  }

  async function handleCreateTask() {
    if (!newTask.title.trim()) {
      setCreateError('Task title is required.');
      return;
    }
    setCreateError('');
    try {
      const taskData = {
        title: newTask.title.trim(),
        description: newTask.description.trim(),
        priority: newTask.priority,
        dueDate: newTask.dueDate || null,
        assignedToId: newTask.assignedToId ? parseInt(newTask.assignedToId) : null
      };
      const result = await createTask(projectId, taskData);
      if (result.status === 'SUCCESS') {
        setNewTask({ title: '', description: '', priority: 'MEDIUM', dueDate: '', assignedToId: '' });
        setShowCreateForm(false);
        fetchTasks();
      } else {
        setCreateError(result.message || 'Failed to create task.');
      }
    } catch (err) {
      setCreateError('Server error. Try again.');
    }
  }

  async function handleStatusChange(taskId, newStatus) {
    try {
      const result = await updateTaskStatus(taskId, newStatus);
      if (result.status === 'SUCCESS') {
        // Update the task in our local state without refetching
        setTasks((prev) =>
          prev.map((t) => (t.taskId === taskId ? { ...t, status: newStatus } : t))
        );
      }
    } catch (err) {
      console.error('Status update failed');
    }
  }

  async function handleUpdateTask() {
    try {
      const result = await updateTask(editingTask.taskId, editTask);
      if (result.status === 'SUCCESS') {
        setEditingTask(null);
        fetchTasks();
      }
    } catch (err) {
      console.error('Update failed');
    }
  }

  async function handleDeleteTask(taskId) {
    if (!window.confirm('Delete this task?')) return;
    try {
      await deleteTask(taskId);
      setTasks((prev) => prev.filter((t) => t.taskId !== taskId));
    } catch (err) {
      console.error('Delete failed');
    }
  }

  // Helper to get badge class based on status
  function getStatusBadgeClass(status) {
    if (status === 'TO_DO') return 'badge badge-todo';
    if (status === 'IN_PROGRESS') return 'badge badge-inprogress';
    if (status === 'DONE') return 'badge badge-done';
    return 'badge';
  }

  function getPriorityBadgeClass(priority) {
    if (priority === 'LOW') return 'badge badge-low';
    if (priority === 'MEDIUM') return 'badge badge-medium';
    if (priority === 'HIGH') return 'badge badge-high';
    return 'badge';
  }

  if (loading) return <p className="loading">Loading tasks...</p>;

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
        <button className="btn-secondary btn-small" onClick={onBack}>← Back</button>
        <h2>Tasks: {projectName}</h2>
        <button className="btn-primary btn-small" onClick={() => setShowCreateForm(!showCreateForm)} style={{ marginLeft: 'auto' }}>
          {showCreateForm ? 'Cancel' : '+ New Task'}
        </button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {/* Create task form */}
      {showCreateForm && (
        <div className="card" style={{ marginBottom: '20px', border: '2px solid #1a73e8' }}>
          <h3 style={{ marginBottom: '12px' }}>Create New Task</h3>
          {createError && <div className="error-msg">{createError}</div>}

          <div className="form-group">
            <label>Title *</label>
            <input
              type="text"
              placeholder="Task title"
              value={newTask.title}
              onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              rows={2}
              placeholder="Task description"
              value={newTask.description}
              onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', gap: '12px' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Priority</label>
              <select value={newTask.priority} onChange={(e) => setNewTask({ ...newTask, priority: e.target.value })}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>

            <div className="form-group" style={{ flex: 1 }}>
              <label>Due Date</label>
              <input
                type="date"
                value={newTask.dueDate}
                onChange={(e) => setNewTask({ ...newTask, dueDate: e.target.value })}
              />
            </div>
          </div>

          {/* Assign to member */}
          {members.length > 0 && (
            <div className="form-group">
              <label>Assign To</label>
              <select value={newTask.assignedToId} onChange={(e) => setNewTask({ ...newTask, assignedToId: e.target.value })}>
                <option value="">-- Not assigned --</option>
                {members.map((m) => (
                  <option key={m.userId} value={m.userId}>{m.userName}</option>
                ))}
              </select>
            </div>
          )}

          <div style={{ display: 'flex', gap: '8px' }}>
            <button className="btn-primary" onClick={handleCreateTask}>Create Task</button>
            <button className="btn-secondary" onClick={() => setShowCreateForm(false)}>Cancel</button>
          </div>
        </div>
      )}

      {/* Task list */}
      {tasks.length === 0 ? (
        <p style={{ color: '#888' }}>No tasks in this project yet. Add your first task!</p>
      ) : (
        tasks.map((task) => (
          <div key={task.taskId} className="card">
            {/* If editing this task */}
            {editingTask?.taskId === task.taskId ? (
              <div>
                <div className="form-group">
                  <label>Title</label>
                  <input value={editTask.title} onChange={(e) => setEditTask({ ...editTask, title: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea rows={2} value={editTask.description || ''} onChange={(e) => setEditTask({ ...editTask, description: e.target.value })} />
                </div>
                <div style={{ display: 'flex', gap: '12px' }}>
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>Priority</label>
                    <select value={editTask.priority} onChange={(e) => setEditTask({ ...editTask, priority: e.target.value })}>
                      <option value="LOW">Low</option>
                      <option value="MEDIUM">Medium</option>
                      <option value="HIGH">High</option>
                    </select>
                  </div>
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>Due Date</label>
                    <input type="date" value={editTask.dueDate || ''} onChange={(e) => setEditTask({ ...editTask, dueDate: e.target.value })} />
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button className="btn-primary btn-small" onClick={handleUpdateTask}>Save</button>
                  <button className="btn-secondary btn-small" onClick={() => setEditingTask(null)}>Cancel</button>
                </div>
              </div>
            ) : (
              <div>
                {/* Task info row */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '8px' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                      <strong>{task.title}</strong>
                      <span className={getStatusBadgeClass(task.status)}>{task.status}</span>
                      <span className={getPriorityBadgeClass(task.priority)}>{task.priority}</span>
                    </div>

                    {task.description && (
                      <p style={{ color: '#555', fontSize: '13px', marginBottom: '4px' }}>{task.description}</p>
                    )}

                    <p style={{ color: '#999', fontSize: '12px' }}>
                      {task.dueDate && `Due: ${task.dueDate} | `}
                      {task.assignedToName ? `Assigned to: ${task.assignedToName}` : 'Unassigned'}
                      {` | Created by: ${task.createdByName}`}
                    </p>
                  </div>

                  {/* Action buttons */}
                  <div style={{ display: 'flex', gap: '6px', alignItems: 'center', flexShrink: 0 }}>
                    {/* Quick status change dropdown */}
                    <select
                      value={task.status}
                      onChange={(e) => handleStatusChange(task.taskId, e.target.value)}
                      style={{ padding: '4px 6px', fontSize: '12px', width: 'auto' }}
                    >
                      <option value="TODO">TODO</option>
                      <option value="IN_PROGRESS">IN PROGRESS</option>
                      <option value="DONE">DONE</option>
                    </select>

                    <button
                      className="btn-secondary btn-small"
                      onClick={() => {
                        setEditingTask(task);
                        setEditTask({ title: task.title, description: task.description, priority: task.priority, dueDate: task.dueDate });
                      }}
                    >
                      Edit
                    </button>
                    <button
                      className="btn-danger btn-small"
                      onClick={() => handleDeleteTask(task.taskId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}
