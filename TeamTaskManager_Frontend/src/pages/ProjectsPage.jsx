// ProjectsPage.jsx
// Shows list of all projects and lets user create/update/delete projects
// Also lets user manage members of each project

import { useState, useEffect } from 'react';
import {
  getAllProjects,
  createProject,
  updateProject,
  deleteProject,
  getProjectMembers,
  addMember,
  removeMember
} from '../services/projectService.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function ProjectsPage({ onViewTasks }) {
  // List of projects
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Form state for creating a new project
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newProjectName, setNewProjectName] = useState('');
  const [newProjectDesc, setNewProjectDesc] = useState('');
  const [createError, setCreateError] = useState('');

  // Edit project state
  const [editingProject, setEditingProject] = useState(null); // holds the project being edited
  const [editName, setEditName] = useState('');
  const [editDesc, setEditDesc] = useState('');

  // Members panel
  const [showMembersFor, setShowMembersFor] = useState(null); // project ID
  const [members, setMembers] = useState([]);
  const [newMemberId, setNewMemberId] = useState('');
  const [memberError, setMemberError] = useState('');

  const { user } = useAuth();

  // Load projects when page opens
  useEffect(() => {
    fetchProjects();
  }, []);

  async function fetchProjects() {
    setLoading(true);
    setError('');
    try {
      const result = await getAllProjects();
      if (result.status === 'SUCCESS') {
        setProjects(result.data || []);
      } else {
        setError('Could not load projects.');
      }
    } catch (err) {
      setError('Could not connect to server.');
    }
    setLoading(false);
  }

  async function handleCreateProject() {
    if (!newProjectName.trim()) {
      setCreateError('Project name is required.');
      return;
    }
    setCreateError('');
    try {
      const result = await createProject(newProjectName.trim(), newProjectDesc.trim());
      if (result.status === 'SUCCESS') {
        setNewProjectName('');
        setNewProjectDesc('');
        setShowCreateForm(false);
        fetchProjects(); // reload the list
      } else {
        setCreateError(result.message || 'Failed to create project.');
      }
    } catch (err) {
      setCreateError('Server error. Try again.');
    }
  }

  async function handleUpdateProject() {
    if (!editName.trim()) return;
    try {
      const result = await updateProject(editingProject.projectId, editName, editDesc);
      if (result.status === 'SUCCESS') {
        setEditingProject(null);
        fetchProjects();
      }
    } catch (err) {
      console.error('Update failed', err);
    }
  }

  async function handleDeleteProject(projectId) {
    if (!window.confirm('Are you sure you want to delete this project?')) return;
    try {
      await deleteProject(projectId);
      fetchProjects();
    } catch (err) {
      console.error('Delete failed', err);
    }
  }

  // Open the members panel for a project
  async function handleShowMembers(projectId) {
    setShowMembersFor(projectId);
    setMemberError('');
    setNewMemberId('');
    try {
      const result = await getProjectMembers(projectId);
      if (result.status === 'SUCCESS') {
        setMembers(result.data || []);
      }
    } catch (err) {
      console.error('Could not load members');
    }
  }

  async function handleAddMember(projectId) {
    if (!newMemberId.trim()) {
      setMemberError('Enter an email or User ID to add.');
      return;
    }
    setMemberError('');
    try {
      // Check if input looks like an email (contains @)
      const isEmail = newMemberId.includes('@');
      const result = await addMember(projectId, isEmail ? newMemberId : parseInt(newMemberId), isEmail);
      if (result.status === 'SUCCESS') {
        setNewMemberId('');
        handleShowMembers(projectId); // refresh members
      } else {
        setMemberError(result.message || 'Failed to add member.');
      }
    } catch (err) {
      setMemberError('Server error.');
    }
  }

  async function handleRemoveMember(projectId, memberId) {
    try {
      await removeMember(projectId, memberId);
      handleShowMembers(projectId); // refresh members
    } catch (err) {
      console.error('Remove member failed');
    }
  }

  if (loading) return <p className="loading">Loading projects...</p>;

  return (
    <div>
      {/* Header row */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>My Projects</h2>
        <button className="btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? 'Cancel' : '+ New Project'}
        </button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {/* Create new project form */}
      {showCreateForm && (
        <div className="card" style={{ marginBottom: '20px', border: '2px solid #1a73e8' }}>
          <h3 style={{ marginBottom: '12px' }}>Create New Project</h3>
          {createError && <div className="error-msg">{createError}</div>}

          <div className="form-group">
            <label>Project Name *</label>
            <input
              type="text"
              placeholder="e.g. Website Redesign"
              value={newProjectName}
              onChange={(e) => setNewProjectName(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea
              rows={2}
              placeholder="Short description (optional)"
              value={newProjectDesc}
              onChange={(e) => setNewProjectDesc(e.target.value)}
            />
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button className="btn-primary" onClick={handleCreateProject}>Create</button>
            <button className="btn-secondary" onClick={() => setShowCreateForm(false)}>Cancel</button>
          </div>
        </div>
      )}

      {/* Projects list */}
      {projects.length === 0 ? (
        <p style={{ color: '#888' }}>No projects yet. Create your first project!</p>
      ) : (
        projects.map((project) => (
          <div key={project.projectId} className="card">
            {/* If editing this project */}
            {editingProject?.projectId === project.projectId ? (
              <div>
                <div className="form-group">
                  <label>Project Name</label>
                  <input value={editName} onChange={(e) => setEditName(e.target.value)} />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea rows={2} value={editDesc} onChange={(e) => setEditDesc(e.target.value)} />
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button className="btn-primary btn-small" onClick={handleUpdateProject}>Save</button>
                  <button className="btn-secondary btn-small" onClick={() => setEditingProject(null)}>Cancel</button>
                </div>
              </div>
            ) : (
              <div>
                {/* Project info */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div>
                    <h3 style={{ marginBottom: '4px' }}>{project.projectName}</h3>
                    <p style={{ color: '#666', fontSize: '13px', marginBottom: '4px' }}>
                      {project.description || 'No description'}
                    </p>
                    <p style={{ color: '#999', fontSize: '12px' }}>
                      Admin: {project.adminUserName} | Created: {new Date(project.createdAt).toLocaleDateString()}
                    </p>
                  </div>

                  {/* Action buttons */}
                  <div style={{ display: 'flex', gap: '6px', flexShrink: 0 }}>
                    <button
                      className="btn-primary btn-small"
                      onClick={() => onViewTasks(project.projectId, project.projectName)}
                    >
                      View Tasks
                    </button>
                    <button
                      className="btn-secondary btn-small"
                      onClick={() => handleShowMembers(project.projectId)}
                    >
                      Members
                    </button>
                    {/* Only admin can edit/delete */}
                    {project.adminUserId === user?.userId && (
                      <>
                        <button
                          className="btn-secondary btn-small"
                          onClick={() => {
                            setEditingProject(project);
                            setEditName(project.projectName);
                            setEditDesc(project.description || '');
                          }}
                        >
                          Edit
                        </button>
                        <button
                          className="btn-danger btn-small"
                          onClick={() => handleDeleteProject(project.projectId)}
                        >
                          Delete
                        </button>
                      </>
                    )}
                  </div>
                </div>

                {/* Members panel - shown below the project card */}
                {showMembersFor === project.projectId && (
                  <div style={{ marginTop: '16px', borderTop: '1px solid #eee', paddingTop: '12px' }}>
                    <h4 style={{ marginBottom: '8px' }}>Project Members</h4>
                    {memberError && <div className="error-msg">{memberError}</div>}

                    {members.length === 0 ? (
                      <p style={{ color: '#888', fontSize: '13px' }}>No extra members yet.</p>
                    ) : (
                      members.map((m) => (
                        <div key={m.userId} style={{ display: 'flex', justifyContent: 'space-between', padding: '4px 0', borderBottom: '1px solid #f0f0f0' }}>
                          <span style={{ fontSize: '13px' }}>{m.userName} ({m.email})</span>
                          {project.adminUserId === user?.userId && (
                            <button
                              className="btn-danger btn-small"
                              onClick={() => handleRemoveMember(project.projectId, m.userId)}
                            >
                              Remove
                            </button>
                          )}
                        </div>
                      ))
                    )}

                    {/* Add member by email or User ID */}
                    {project.adminUserId === user?.userId && (
                      <div style={{ marginTop: '10px', display: 'flex', gap: '8px' }}>
                        <input
                          type="text"
                          placeholder="Enter email to add (e.g. bob@test.com)"
                          value={newMemberId}
                          onChange={(e) => setNewMemberId(e.target.value)}
                          style={{ width: '250px' }}
                        />
                        <button className="btn-primary btn-small" onClick={() => handleAddMember(project.projectId)}>
                          Add Member
                        </button>
                        <button className="btn-secondary btn-small" onClick={() => setShowMembersFor(null)}>
                          Close
                        </button>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}
