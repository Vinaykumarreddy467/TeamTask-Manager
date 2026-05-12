// AuthContext.jsx
// This is a Context - it lets any component in the app access the logged-in user
// without passing props through every level

import { createContext, useState, useContext } from 'react';

// Step 1: Create the context (like a "global store")
const AuthContext = createContext(null);

// Step 2: Create the Provider - this wraps the whole app and provides the value
export function AuthProvider({ children }) {
  // State to store the logged-in user info
  const [user, setUser] = useState(() => {
    // When app first loads, check if user was already logged in
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  // Function to log in - called after successful login API call
  function loginUser(userData, token) {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  }

  // Function to log out - clear everything
  function logoutUser() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }

  // The value we share with all components
  const value = {
    user,          // the logged-in user object (or null if not logged in)
    loginUser,     // function to call after login
    logoutUser,    // function to call to logout
    isLoggedIn: user !== null  // boolean: true if someone is logged in
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

// Step 3: Custom hook - easy way to use the context in any component
// Usage: const { user, loginUser, logoutUser } = useAuth();
export function useAuth() {
  return useContext(AuthContext);
}
