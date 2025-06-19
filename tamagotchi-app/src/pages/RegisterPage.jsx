import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import AuthForm from '../components/auth/AuthForm';

const RegisterPage = () => {
    const { register } = useAuth();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleRegister = async (credentials) => {
        if (credentials.password.length < 4) {
            setError('Password must be at least 4 characters long.');
            return;
        }
        setError('');
        setLoading(true);
        try {
            // The register function now automatically logs the user in
            await register(credentials.username, credentials.password);
        } catch (err) {
            setError(err.response?.data?.message || 'That username is already taken.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
            <AuthForm
                isRegister={true}
                onSubmit={handleRegister}
                error={error}
                loading={loading}
            />
        </div>
    );
};

export default RegisterPage;