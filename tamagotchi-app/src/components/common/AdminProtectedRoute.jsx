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

  const isAdmin = user.role === 'ROLE_ADMIN';

  if (!isAdmin) {
    return <Navigate to="/pets" replace />;
  }

  return children;
};

export default AdminProtectedRoute;