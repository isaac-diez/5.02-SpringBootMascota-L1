import React, { useState } from 'react';

const AuthForm = ({ onSubmit, buttonText }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const handleSubmit = (e) => { e.preventDefault(); onSubmit({ username, password }); };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <div>
                <label htmlFor="username" className="text-sm font-bold block text-left mb-1">Username</label>
                <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} required className="tamagotchi-input" autoComplete="username" />
            </div>
            <div>
                <label htmlFor="password" className="text-sm font-bold block text-left mb-1">Password</label>
                <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required className="tamagotchi-input" autoComplete="current-password" />
            </div>
            <div className="flex justify-center pt-2">
                 <button type="submit" className="btn btn-primary">{buttonText}</button>
            </div>
        </form>
    );
};

export default AuthForm;