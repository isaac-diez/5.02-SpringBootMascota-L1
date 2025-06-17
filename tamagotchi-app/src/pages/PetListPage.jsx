import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/apiClient';
import PetCard from '../components/pets/PetCard';
import CreatePetForm from '../components/pets/CreatePetForm';

const PetListPage = () => {
    const { user, logout } = useAuth();
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isCreating, setIsCreating] = useState(false);

    useEffect(() => {
        const fetchPets = async () => {
            try {
                setLoading(true);
                const response = await apiClient.get('/pet/my-pets');
                setPets(response.data || []);
            } catch (err) {
                console.error("Error fetching pets:", err);
                setError('Could not load your pets.');
            } finally {
                setLoading(false);
            }
        };
        fetchPets();
    }, []);

    const handlePetCreated = (newPet) => {
        setPets(currentPets => [...currentPets, newPet]);
        setIsCreating(false);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Loading...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;

    return (
        <>
            <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <header className="flex flex-col sm:flex-row justify-between items-center mb-8 gap-4">
                    <h1 className="font-pixel text-3xl sm:text-4xl">My Pets</h1>
                    <div className="flex items-center gap-4">
                        <span className="font-bold sm:inline">Hi, {user?.username}</span>
                        <button onClick={() => setIsCreating(true)} className="btn btn-primary">New Pet</button>
                        <button onClick={logout} className="btn btn-danger">Logout</button>
                    </div>
                </header>
                <main>
                    {pets.length === 0? (
                         <div className="text-center mt-20 tamagotchi-container max-w-md mx-auto">
                            <h2 className="font-pixel text-2xl mb-4">No pets yet!</h2>
                            <p className="mb-6">Click "New Pet" to create your first digital friend.</p>
                         </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                            {pets.map(pet => (<PetCard key={pet.id} pet={pet} />))}
                        </div>
                    )}
                </main>
            </div>
            {isCreating && <CreatePetForm onPetCreated={handlePetCreated} onCancel={() => setIsCreating(false)} />}
        </>
    );
};

export default PetListPage;