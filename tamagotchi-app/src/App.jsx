import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import AdminProtectedRoute from './components/common/AdminProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import PetListPage from './pages/PetListPage';
import PetDetailPage from './pages/PetDetailPage';
import AdminPage from './pages/AdminPage';

function App() {
  return (
    // FIX: The <Router> must wrap the <AuthProvider>
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/pets" element={<ProtectedRoute><PetListPage /></ProtectedRoute>} />
          <Route path="/pets/:petId" element={<ProtectedRoute><PetDetailPage /></ProtectedRoute>} />
          <Route path="/admin" element={<AdminProtectedRoute><AdminPage /></AdminProtectedRoute>} />
          <Route path="/" element={<Navigate to="/pets" replace />} />
          <Route path="*" element={<Navigate to="/pets" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;