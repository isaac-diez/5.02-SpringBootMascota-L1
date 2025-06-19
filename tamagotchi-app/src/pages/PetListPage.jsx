import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/apiClient';
import PetCard from '../components/pets/PetCard';
import CreatePetForm from '../components/pets/CreatePetForm';

// --- NEW HELPER FUNCTION ---
// This is the same function from AdminPage. It derives the 'healthState' string
// that your PetImage component needs from the data we have available.
const deriveHealthState = (pet) => {
  if (pet.evolutionState === 'DEAD') return 'dead';
  if (pet.healthLevel < 10) return 'sick';
  if (pet.healthLevel < 30) return 'weak';
  if (pet.healthLevel < 65) return 'ok';
  if (pet.healthLevel < 85) return 'fit';
  return 'strong';
};


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
                // NOTE: The user's original file used `/pet/my-pets`, which is correct for this page.
                const response = await apiClient.get('/pet/my-pets');
                const petsFromApi = response.data || [];

                // --- FIX ---
                // We transform the pet data here, adding the 'healthState' property
                // before setting the state. This makes the data compatible with your components.
                const petsWithDerivedState = petsFromApi.map(pet => ({
                    ...pet,
                    healthState: deriveHealthState(pet)
                }));

                setPets(petsWithDerivedState);

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
        // Also transform the newly created pet so its image shows correctly right away.
        const newPetWithState = {
            ...newPet,
            healthState: deriveHealthState(newPet)
        };
        setPets(currentPets => [...currentPets, newPetWithState]);
        setIsCreating(false);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Loading...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;

    return (
        <>
            <nav className="bg-white/70 backdrop-blur-md shadow-sm sticky top-0 z-40">
                <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        <div className="flex-shrink-0">
                             <span className="font-bold">Hi, {user?.username}</span>
                        </div>
                        <div className="flex items-center gap-4">
                            <button onClick={() => setIsCreating(true)} className="btn btn-primary">New Pet</button>
                            <button onClick={logout} className="btn btn-danger">Logout</button>
                        </div>
                    </div>
                </div>
            </nav>
            <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="font-pixel text-3xl sm:text-4xl text-center mb-8">My Pets</h1>
                <main>
                    {pets.length === 0 ? (
                         <div className="text-center mt-20 tamagotchi-container max-w-md mx-auto">
                            <h2 className="font-pixel text-2xl mb-4">No pets yet!</h2>
                            <p className="mb-6">Click "New Pet" to create your first digital friend.</p>
                         </div>
                    ) : (
                        <div className="grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                            {/* This part remains unchanged. It now passes the enhanced pet object to PetCard. */}
                            {pets.map(pet => (<PetCard key={pet.petId} pet={pet} />))}
                        </div>
                    )}
                </main>
            </div>
            {isCreating && <CreatePetForm onPetCreated={handlePetCreated} onCancel={() => setIsCreating(false)} />}
        </>
    );
};

export default PetListPage;