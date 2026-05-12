// Navbar.jsx
// Top navigation bar - shown when user is logged in
// Uses useAuth to get user info and logout function

import { useAuth } from '../context/AuthContext.jsx';

export default function Navbar({ currentPage, onNavigate }) {
  const { user, logoutUser } = useAuth();

  function handleLogout() {
    logoutUser();
    // After logout, the App will automatically show the login page
    // because isLoggedIn becomes false
  }

  const navStyle = {
    backgroundColor: '#1a73e8',
    color: 'white',
    padding: '12px 20px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const linkStyle = {
    color: 'white',
    background: 'none',
    border: 'none',
    padding: '6px 12px',
    cursor: 'pointer',
    fontSize: '14px',
    borderRadius: '4px',
    textDecoration: 'none'
  };

  const activeLinkStyle = {
    ...linkStyle,
    backgroundColor: 'rgba(255,255,255,0.2)'
  };

  return (
    <nav style={navStyle}>
      {/* Left side - App name */}
      <span style={{ fontWeight: 'bold', fontSize: '16px' }}>
        Team Task Manager
      </span>

      {/* Middle - Navigation links */}
      <div style={{ display: 'flex', gap: '4px' }}>
        <button
          style={currentPage === 'dashboard' ? activeLinkStyle : linkStyle}
          onClick={() => onNavigate('dashboard')}
        >
          Dashboard
        </button>
        <button
          style={currentPage === 'projects' ? activeLinkStyle : linkStyle}
          onClick={() => onNavigate('projects')}
        >
          Projects
        </button>
      </div>

      {/* Right side - User info and logout */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <span style={{ fontSize: '13px' }}>
          Hello, {user?.name}
        </span>
        <button
          style={{ ...linkStyle, backgroundColor: 'rgba(255,255,255,0.15)', border: '1px solid rgba(255,255,255,0.4)' }}
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
