import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import AuthForm from '../components/auth/AuthForm';
import AuthLayout from '../components/layout/AuthLayout'; // <-- Import the new layout

const LoginPage = () => {
    const { login } = useAuth();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLogin = async (credentials) => {
        setError('');
        setLoading(true);
        try {
            await login(credentials.username, credentials.password);
        } catch (err) {
            setError('Invalid username or password.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayout>
                <AuthForm
                    isRegister={false}
                    onSubmit={handleLogin}
                    error={error}
                    loading={loading}
                />
        </AuthLayout>
    );
};

export default LoginPage;