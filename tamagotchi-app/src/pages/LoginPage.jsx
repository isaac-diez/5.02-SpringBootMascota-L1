import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AuthForm from '../components/auth/AuthForm';

const LoginPage = () => {
    const { login } = useAuth();
    const [error, setError] = useState('');

    const handleLogin = async (credentials) => {
        try {
            await login(credentials.username, credentials.password);
            setError('');
        } catch (err) {
            setError('Login failed. Check your credentials.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            <div className="tamagotchi-container w-25 max-w-sm">
                <h1 className="font-pixel text-3xl text-center mb-2">Login</h1>
                <p className="text-center text-gray-600 mb-6">Welcome back!</p>
                <AuthForm onSubmit={handleLogin} buttonText="Login"/>
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6">
                    <p className="text-gray-600">No account? <Link to="/register" className="font-bold text-[var(--tam-pink)] hover:underline">Register</Link></p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;