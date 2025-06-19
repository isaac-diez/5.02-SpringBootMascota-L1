import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import AuthForm from '../components/auth/AuthForm';

const LoginPage = () => {
    const { login } = useAuth();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLogin = async (credentials) => {
        setError('');
        setLoading(true);
        try {
            await login(credentials.username, credentials.password);
            // Navigation is handled inside the login function in AuthContext
        } catch (err) {
            setError('Invalid username or password.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
            <AuthForm
                isRegister={false}
                onSubmit={handleLogin}
                error={error}
                loading={loading}
            />
        </div>
    );
};

export default LoginPage;