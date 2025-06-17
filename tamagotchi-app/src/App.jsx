import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext.jsx';

// Import Pages
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import PetListPage from './pages/PetListPage.jsx';
import PetDetailPage from './pages/PetDetailPage.jsx';

// Import Common Components
import ProtectedRoute from './components/common/ProtectedRoute.jsx';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Protected Routes */}
            <Route path="/pets" element={<ProtectedRoute><PetListPage /></ProtectedRoute>} />
            <Route path="/pets/:petId" element={<ProtectedRoute><PetDetailPage /></ProtectedRoute>} />

            {/* Default Redirect */}
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;