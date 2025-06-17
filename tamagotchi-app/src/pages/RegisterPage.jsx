import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AuthForm from '../components/auth/AuthForm';

const RegisterPage = () => {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');

    const handleRegister = async (credentials) => {
        try {
            await register(credentials.username, credentials.password);
            setError('');
            navigate('/login?registered=true');
        } catch (err) {
            setError('Registration failed.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            <div className="tamagotchi-container w-full max-w-sm">
                <h1 className="font-pixel text-3xl text-center mb-2">Register</h1>
                <p className="text-center text-gray-600 mb-6">Create your account!</p>
                <AuthForm onSubmit={handleRegister} buttonText="Register"/>
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6">
                    <p className="text-gray-600">Have an account? <Link to="/login" className="font-bold text-[var(--tam-pink)] hover:underline">Login</Link></p>
                </div>
            </div>
        </div>
    );
};

export default RegisterPage;