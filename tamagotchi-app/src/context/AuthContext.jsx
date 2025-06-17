import React, { useState, useEffect, createContext, useContext, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/apiClient.js';

const AuthContext = createContext(null);

const parseJwt = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  // FIXED: Added the missing [token, setToken] variable names
  const [token, setToken] = useState(() => localStorage.getItem('jwtToken'));
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      const decodedToken = parseJwt(token);
      if (decodedToken && decodedToken.exp * 1000 > Date.now()) {
        setUser({ username: decodedToken.sub, role: decodedToken.role });
      } else {
        localStorage.removeItem('jwtToken');
        setToken(null);
      }
    }
  }, [token]);

  const login = async (username, password) => {
    try {
      const response = await apiClient.post('/auth/login', { username, password });
      if (response.data && response.data.token) {
        const newToken = response.data.token;
        localStorage.setItem('jwtToken', newToken);
        setToken(newToken);
        navigate('/pets');
      }
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const register = async (username, password) => {
    try {
      await apiClient.post('/auth/register', { username, password });
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  };

  const createPet = async (petData) => {
    try {
      const response = await apiClient.post('/pet/new', petData);
      return response.data;
    } catch (error) {
      console.error("Error creating pet:", error);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  const contextValue = useMemo(
    () => ({ token, user, isAuthenticated: !!token, login, logout, register, createPet }),
    [token, user]
  );

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);