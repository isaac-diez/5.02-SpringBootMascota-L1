import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import PetDisplay from '../components/pets/PetDisplay';
import PetStats from '../components/pets/PetStats';
import PetActions from '../components/pets/PetActions';
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

const PetDetailPage = () => {
    const { user, token, logout } = useAuth();
    const { petId } = useParams();
    const navigate = useNavigate();

    const [pet, setPet] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPetDetails = async () => {
            if (!petId) {
                setError('No pet ID provided.');
                setLoading(false);
                return;
            }
            try {
                setLoading(true);
                const response = await apiClient.get(`/pet/get/${petId}`);
                setPet(response.data);
            } catch (err) {
                setError('Could not find your pet.');
            } finally {
                setLoading(false);
            }
        };
        fetchPetDetails();
    }, [petId]);

    useEffect(() => {
        if (user && token && pet) { // Only connect if we have a user, token, AND a pet loaded
            const client = new Client({
                webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
                connectHeaders: { Authorization: `Bearer ${token}` },
                reconnectDelay: 5000,
            });

            client.onConnect = (frame) => {
                console.log('PetDetail Page: STOMP Connected!');
                // Subscribe to the same user-specific queue
                client.subscribe(`/user/${user.username}/queue/pet-updates`, (message) => {
                    const updatedPetFromWS = JSON.parse(message.body);

                    // IMPORTANT: Only update if the message is for the pet we are currently viewing
                    if (updatedPetFromWS.petId == petId) {
                        console.log('Received real-time update for this pet:', updatedPetFromWS);
                        // Re-derive the healthState from the new data
                        const petWithDerivedState = { ...updatedPetFromWS, healthState: deriveHealthState(updatedPetFromWS) };
                        setPet(petWithDerivedState);
                    }
                });
            };

            client.activate();

            // Cleanup function to disconnect when the component unmounts or the pet changes
            return () => {
                client.deactivate();
                console.log('PetDetail Page: STOMP Disconnected.');
            };
        }
    }, [user, token, pet, petId]); // Dependencies ensure we reconnect if needed

    const handlePetUpdate = (updatedPet) => {
        setPet(updatedPet);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Finding your pet...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;
    if (!pet) return <div className="text-center font-pixel text-2xl p-8">Pet not found.</div>;

    return (
        <div className="w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <header className="flex justify-between items-center mb-8">
                <button onClick={() => navigate('/pets')} className="btn btn-primary">
                    &larr; Back to List
                </button>
                <h1 className="font-pixel text-3xl sm:text-4xl text-center">{pet.name}</h1>
                <button onClick={logout} className="btn btn-danger">Logout</button>
            </header>

            <main>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12 items-start">

                    {/* Left Column for the Image */}
                    <PetDisplay pet={pet} />

                    {/* Right Column for Stats and Actions */}
                    <div className="space-y-8 w-full">
                        <PetStats pet={pet} />
                        <PetActions pet={pet} onAction={handlePetUpdate} />
                    </div>
                </div>
            </main>
        </div>
    );
};

export default PetDetailPage;