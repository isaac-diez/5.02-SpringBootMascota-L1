import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/apiClient';
import PetCard from '../components/pets/PetCard';
import CreatePetForm from '../components/pets/CreatePetForm';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';


const deriveHealthState = (pet) => {
  if (pet.healthState === 'DEAD') return 'dead';
  if (pet.healthState === 'SICK') return 'sick';
  if (pet.healthState === 'WEAK') return 'weak';
  if (pet.healthState === 'OK') return 'ok';
  if (pet.healthState === 'FIT') return 'fit';
  return 'strong';
};


const PetListPage = () => {
    const { user, token, logout } = useAuth();
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isCreating, setIsCreating] = useState(false);
    const [notification, setNotification] = useState('');

    useEffect(() => {
        const fetchPets = async () => {
            try {
                setLoading(true);
                const response = await apiClient.get('/pet/my-pets');
                const petsFromApi = response.data || [];
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

    useEffect(() => {
                // Only connect if we have a user and a token
                if (user && token) {
                    // Create a new STOMP client over a SockJS connection
                    const client = new Client({
                        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
                        connectHeaders: {
                            Authorization: `Bearer ${token}`,
                        },
                        debug: (str) => {
                            console.log('STOMP: ' + str);
                        },
                        reconnectDelay: 5000,
                    });

                    client.onConnect = (frame) => {
                        console.log('Connected: ' + frame);
                        // Subscribe to the user-specific queue.
                        // The backend sends messages here for this user's pets.
                        client.subscribe(`/user/${user.username}/queue/pet-updates`, (message) => {
                            const updatedPet = JSON.parse(message.body);
                            console.log('Received pet update:', updatedPet);

                            // Find and update the pet in the local state
                            setPets(prevPets =>
                                prevPets.map(p =>
                                    p.petId === updatedPet.petId
                                        ? { ...p, ...updatedPet, healthState: deriveHealthState(updatedPet) } // Update pet with new data
                                        : p
                                )
                            );
                        });
                    };

                    client.onStompError = (frame) => {
                        console.error('Broker reported error: ' + frame.headers['message']);
                        console.error('Additional details: ' + frame.body);
                    };

                    // Activate the client
                    client.activate();

                    // Return a cleanup function to disconnect when the component unmounts
                    return () => {
                        client.deactivate();
                        console.log('Disconnected STOMP client');
                    };
                }
            }, [user, token]); // Rerun this effect if the user or token changes

    const handlePetCreated = (newPet) => {

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