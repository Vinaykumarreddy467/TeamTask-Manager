// DashboardPage.jsx
// Shows stats about tasks - total, todo, in progress, done, overdue
// Calls dashboardService.getDashboardOverview() when page loads using useEffect

import { useState, useEffect } from 'react';
import { getDashboardOverview, getOverdueTasks } from '../services/dashboardService.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function DashboardPage() {
  // State to store the dashboard data from API
  const [dashboard, setDashboard] = useState(null);
  const [overdueTasks, setOverdueTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const { user } = useAuth();

  // useEffect - runs when this component first appears on screen
  // It's like "on page load, fetch data"
  useEffect(() => {
    fetchDashboardData();
  }, []); // [] means run only once when component mounts

  async function fetchDashboardData() {
    setLoading(true);
    setError('');
    try {
      const result = await getDashboardOverview();
      if (result.status === 'SUCCESS') {
        setDashboard(result.data);
      } else {
        setError('Failed to load dashboard data.');
      }

      const overdueResult = await getOverdueTasks();
      if (overdueResult.status === 'SUCCESS') {
        setOverdueTasks(overdueResult.data || []);
      }
    } catch (err) {
      setError('Could not connect to server.');
    }
    setLoading(false);
  }

  if (loading) return <p className="loading">Loading dashboard...</p>;

  return (
    <div>
      <h2 style={{ marginBottom: '20px' }}>Welcome, {user?.name}!</h2>

      {error && <div className="error-msg">{error}</div>}

      {/* Stats Cards */}
      {dashboard && (
        <div>
          <h3 style={{ marginBottom: '12px', color: '#555' }}>Task Overview</h3>
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginBottom: '24px' }}>
            <StatCard label="Total Tasks" value={dashboard.totalTasks} color="#1a73e8" />
            <StatCard label="To Do" value={dashboard.toDoCount} color="#3949ab" />
            <StatCard label="In Progress" value={dashboard.inProgressCount} color="#e65100" />
            <StatCard label="Done" value={dashboard.doneCount} color="#137333" />
            <StatCard label="Overdue" value={dashboard.overdueCount} color="#d93025" />
          </div>

          {/* Tasks per user table */}
          {dashboard.tasksPerUser && Object.keys(dashboard.tasksPerUser).length > 0 && (
            <div className="card" style={{ marginBottom: '24px' }}>
              <h3 style={{ marginBottom: '12px' }}>Tasks Per Team Member</h3>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                    <th style={{ padding: '8px' }}>Member</th>
                    <th style={{ padding: '8px' }}>Task Count</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(dashboard.tasksPerUser).map(([memberName, count]) => (
                    <tr key={memberName} style={{ borderBottom: '1px solid #eee' }}>
                      <td style={{ padding: '8px' }}>{memberName}</td>
                      <td style={{ padding: '8px' }}>{count}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Overdue Tasks */}
      {overdueTasks.length > 0 && (
        <div className="card">
          <h3 style={{ marginBottom: '12px', color: '#d93025' }}>⚠ Overdue Tasks</h3>
          {overdueTasks.map((task) => (
            <div key={task.taskId} style={{ borderBottom: '1px solid #eee', padding: '8px 0' }}>
              <strong>{task.title}</strong>
              <span style={{ marginLeft: '10px', color: '#888', fontSize: '12px' }}>
                Due: {task.dueDate} | Priority: {task.priority}
              </span>
              {task.assignedToName && (
                <span style={{ marginLeft: '10px', color: '#666', fontSize: '12px' }}>
                  Assigned to: {task.assignedToName}
                </span>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Refresh button */}
      <div style={{ marginTop: '16px' }}>
        <button className="btn-secondary" onClick={fetchDashboardData}>
          Refresh Data
        </button>
      </div>
    </div>
  );
}

// Small component - one stat card
function StatCard({ label, value, color }) {
  return (
    <div style={{
      backgroundColor: 'white',
      border: `2px solid ${color}`,
      borderRadius: '6px',
      padding: '16px 20px',
      minWidth: '120px',
      textAlign: 'center'
    }}>
      <div style={{ fontSize: '28px', fontWeight: 'bold', color: color }}>{value}</div>
      <div style={{ fontSize: '12px', color: '#555', marginTop: '4px' }}>{label}</div>
    </div>
  );
}
