// App.jsx
// Main App component - controls which page is shown based on login state
// Uses useState to track the current "page" (we use simple string routing, no router library)

import { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext.jsx';
import LoginPage from './pages/LoginPage.jsx';
import SignupPage from './pages/SignupPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import ProjectsPage from './pages/ProjectsPage.jsx';
import TasksPage from './pages/TasksPage.jsx';
import Navbar from './components/Navbar.jsx';

// Inner app that can access AuthContext
function AppInner() {
  const { isLoggedIn } = useAuth();

  // Simple page state - instead of a URL router, we track the page as a string
  // page can be: 'login', 'signup', 'dashboard', 'projects', 'tasks'
  const [page, setPage] = useState('login');

  // selectedProjectId is used when user clicks on a project to view its tasks
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [selectedProjectName, setSelectedProjectName] = useState('');

  // Navigate to a page
  function goTo(pageName, projectId = null, projectName = '') {
    setPage(pageName);
    if (projectId) {
      setSelectedProjectId(projectId);
      setSelectedProjectName(projectName);
    }
  }

  // If not logged in, only show login or signup page
  if (!isLoggedIn) {
    if (page === 'signup') {
      return <SignupPage onGoToLogin={() => goTo('login')} />;
    }
    return <LoginPage onGoToSignup={() => goTo('signup')} onLoginSuccess={() => goTo('dashboard')} />;
  }

  // If logged in, show the full app with navbar
  return (
    <div>
      <Navbar currentPage={page} onNavigate={goTo} />

      <div style={{ padding: '20px', maxWidth: '900px', margin: '0 auto' }}>
        {page === 'dashboard' && <DashboardPage />}
        {page === 'projects' && (
          <ProjectsPage
            onViewTasks={(projectId, projectName) => goTo('tasks', projectId, projectName)}
          />
        )}
        {page === 'tasks' && selectedProjectId && (
          <TasksPage
            projectId={selectedProjectId}
            projectName={selectedProjectName}
            onBack={() => goTo('projects')}
          />
        )}
        {/* Default fallback - show dashboard */}
        {(page === 'login' || page === 'signup') && <DashboardPage />}
      </div>
    </div>
  );
}

// Wrap AppInner with AuthProvider so all components can access auth state
export default function App() {
  return (
    <AuthProvider>
      <AppInner />
    </AuthProvider>
  );
}
