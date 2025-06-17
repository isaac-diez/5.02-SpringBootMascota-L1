import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const PageLayout = ({ children }) => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* This header contains the back and logout buttons */}
      <header className="p-4 flex justify-between items-center">
        <button onClick={() => navigate('/pets')} className="btn">
            &larr; Back to My Pets
        </button>
        <button onClick={logout} className="btn btn-danger">
          Logout
        </button>
      </header>

      {/* The rest of your page content will be rendered here */}
      <main className="px-4 sm:px-6 lg:px-8 pb-8">
        {children}
      </main>
    </div>
  );
};

export default PageLayout;