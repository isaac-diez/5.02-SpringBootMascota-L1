import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const AdminProtectedRoute = ({ children }) => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return <div className="text-center font-pixel text-2xl p-8">Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // --- FIX: Simplified check for Admin Role ---
  // This logic now correctly assumes the role is a string.
  const isAdmin = user.role === 'ROLE_ADMIN';

  if (!isAdmin) {
    // If a non-admin user tries to access /admin, send them to their pets page.
    return <Navigate to="/pets" replace />;
  }

  return children;
};

export default AdminProtectedRoute;