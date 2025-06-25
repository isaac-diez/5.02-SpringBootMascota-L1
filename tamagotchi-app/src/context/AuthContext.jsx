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
  const [token, setToken] = useState(() => localStorage.getItem('jwtToken'));
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = localStorage.getItem('jwtToken');
    if (storedToken) {
      const decodedToken = parseJwt(storedToken);
      if (decodedToken && decodedToken.exp * 1000 > Date.now()) {
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
        setUser({ username: decodedToken.sub, role: decodedToken.role });
      } else {
        localStorage.removeItem('jwtToken');
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const response = await apiClient.post('/auth/login', { username, password });
      if (response.data && response.data.token) {
        const newToken = response.data.token;
        localStorage.setItem('jwtToken', newToken);
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        const decodedToken = parseJwt(newToken);

        setUser({ username: decodedToken.sub, role: decodedToken.role });
        setToken(newToken);

        if (decodedToken && decodedToken.role && decodedToken.role === 'ROLE_ADMIN') {
          navigate('/admin');
        } else {
          navigate('/pets');
        }
      }
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const register = async (username, password) => {
      await apiClient.post('/auth/register', { username, password });
      await login(username, password);
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
    delete apiClient.defaults.headers.common['Authorization'];
    navigate('/login');
  };

  const contextValue = useMemo(
    () => ({ token, user, isLoading, login, logout, register, createPet }),
    [token, user, isLoading]
  );

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);