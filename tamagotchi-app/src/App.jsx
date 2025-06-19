import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import PetListPage from './pages/PetListPage';
import PetDetailPage from './pages/PetDetailPage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route path="/pets" element={<PetListPage />} />

          {/* FIX: Use a consistent parameter name, e.g., :petId */}
          <Route path="/pets/:petId" element={<PetDetailPage />} />

          <Route path="/" element={<Navigate to="/pets" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;