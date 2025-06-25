import React, { useState } from 'react';
import { Link } from 'react-router-dom';

const AuthForm = ({ isRegister, onSubmit, error, loading }) => {
    const [credentials, setCredentials] = useState({ username: '', password: '' });

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSubmit(credentials);
    };

    return (
        <div className="tamagotchi-container">
            <h1 className="font-pixel text-4xl text-center mb-6">{isRegister ? 'Register' : 'Login'}</h1>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="username" className="text-sm font-bold block text-left mb-1">Username</label>
                    <input
                        id="username"
                        name="username"
                        type="text"
                        value={credentials.username}
                        onChange={handleChange}
                        required
                        className="tamagotchi-input"
                    />
                </div>
                <div>
                    <label htmlFor="password" className="text-sm font-bold block text-left mb-1">Password</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        value={credentials.password}
                        onChange={handleChange}
                        required
                        className="tamagotchi-input"
                    />
                </div>
                {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                <div className="pt-4">
                    <button type="submit" disabled={loading} className="btn btn-primary w-full">
                        {loading ? '...' : (isRegister ? 'Create Account' : 'Enter')}
                    </button>
                </div>
                <p className="text-center text-sm pt-4">
                    {isRegister ? (
                        <>Already have an account? <Link to="/login" className="underline font-bold">Login</Link></>
                    ) : (
                        <>Don't have an account? <Link to="/register" className="underline font-bold">Sign Up</Link></>
                    )}
                </p>
            </form>
        </div>
    );
};

export default AuthForm;