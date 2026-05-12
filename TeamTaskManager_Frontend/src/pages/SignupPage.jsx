// SignupPage.jsx
// Signup form - calls authService.signup() on submit
// After successful signup, the user is logged in automatically

import { useState } from 'react';
import { signup } from '../services/authService.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function SignupPage({ onGoToLogin }) {
  // Form field states
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // UI state
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const { loginUser } = useAuth();

  async function handleSignup() {
    // Validation
    if (!name.trim()) {
      setError('Name is required.');
      return;
    }
    if (!email.trim()) {
      setError('Email is required.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setError('');
    setLoading(true);

    try {
      const result = await signup(name, email, password);

      if (result.status === 'SUCCESS') {
        const { token, userId, name: userName, email: userEmail } = result.data;
        setSuccess(true);
        // Wait a moment, then login the user
        setTimeout(() => {
          loginUser({ userId, name: userName, email: userEmail }, token);
        }, 1000);
      } else {
        setError(result.message || 'Signup failed. Try again.');
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
        <h2 style={{ marginBottom: '20px', color: '#1a73e8' }}>Create Account</h2>

        {error && <div className="error-msg">{error}</div>}
        {success && <div className="success-msg">Account created! Logging you in...</div>}

        <div className="form-group">
          <label>Full Name</label>
          <input
            type="text"
            placeholder="John Doe"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Password (min 6 characters)</label>
          <input
            type="password"
            placeholder="Choose a password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSignup()}
          />
        </div>

        <button
          className="btn-primary"
          style={{ width: '100%', padding: '10px', marginTop: '8px' }}
          onClick={handleSignup}
          disabled={loading || success}
        >
          {loading ? 'Creating account...' : 'Sign Up'}
        </button>

        <p style={{ marginTop: '16px', textAlign: 'center', fontSize: '13px' }}>
          Already have an account?{' '}
          <button
            style={{ background: 'none', border: 'none', color: '#1a73e8', cursor: 'pointer', padding: 0, fontSize: '13px' }}
            onClick={onGoToLogin}
          >
            Log in
          </button>
        </p>
      </div>
    </div>
  );
}
