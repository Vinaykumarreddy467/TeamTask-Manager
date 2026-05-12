// LoginPage.jsx
// Login form - calls authService.login() on submit
// Uses useState for form fields and error messages

import { useState } from 'react';
import { login } from '../services/authService.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function LoginPage({ onGoToSignup, onLoginSuccess }) {
  // State for the form fields
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // State for showing errors or loading
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Get loginUser function from our AuthContext
  const { loginUser } = useAuth();

  // This runs when user clicks the Login button
  async function handleLogin() {
    // Basic validation
    if (!email || !password) {
      setError('Please enter email and password.');
      return;
    }

    setError('');
    setLoading(true);

    try {
      // Call the login API from authService.js
      const result = await login(email, password);

      if (result.status === 'SUCCESS') {
        // result.data has: token, userId, name, email
        const { token, userId, name, email: userEmail } = result.data;
        loginUser({ userId, name, email: userEmail }, token);
        onLoginSuccess(); // tell App to go to dashboard
      } else {
        setError(result.message || 'Login failed. Check your credentials.');
      }
    } catch (err) {
      setError('Could not connect to server. Make sure the backend is running.');
    }

    setLoading(false);
  }

  const containerStyle = {
    minHeight: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5'
  };

  const boxStyle = {
    backgroundColor: 'white',
    padding: '32px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    width: '360px'
  };

  return (
    <div style={containerStyle}>
      <div style={boxStyle}>
        <h2 style={{ marginBottom: '20px', color: '#1a73e8' }}>Login</h2>
        <p style={{ marginBottom: '20px', color: '#666', fontSize: '13px' }}>
          Welcome back! Please enter your details.
        </p>

        {/* Show error if any */}
        {error && <div className="error-msg">{error}</div>}

        {/* Email field */}
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        {/* Password field */}
        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            placeholder="Your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
          />
        </div>

        {/* Login button */}
        <button
          className="btn-primary"
          style={{ width: '100%', padding: '10px', marginTop: '8px' }}
          onClick={handleLogin}
          disabled={loading}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>

        {/* Link to signup */}
        <p style={{ marginTop: '16px', textAlign: 'center', fontSize: '13px' }}>
          Don't have an account?{' '}
          <button
            style={{ background: 'none', border: 'none', color: '#1a73e8', cursor: 'pointer', padding: 0, fontSize: '13px' }}
            onClick={onGoToSignup}
          >
            Sign up
          </button>
        </p>
      </div>
    </div>
  );
}
