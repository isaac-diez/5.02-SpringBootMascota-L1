import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import PetImage from '../components/pets/PetImage';
import PetStats from '../components/pets/PetStats';
import PetActions from '../components/pets/PetActions';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Notification from '../components/common/Notification';

// --- FIX #1: Using the more robust helper function ---
// This function correctly derives the state from the pet's underlying levels,
// just like we did on the other pages.
const deriveHealthState = (pet) => {
  if (!pet) return 'ok';
  if (pet.evolutionState === 'DEAD') return 'dead';
  if (pet.healthState === 'SICK' || pet.levels.health <= 10) return 'sick';
  if (pet.healthState === 'WEAK' || pet.levels.hungry >= 80) return 'weak';
  if (pet.healthState === 'FIT') return 'fit';
  if (pet.healthState === 'STRONG') return 'strong';
  return 'ok';
};


const PetDetailPage = () => {
    const { user, token, logout } = useAuth();
    const { petId } = useParams();
    const navigate = useNavigate();

    const [pet, setPet] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [notification, setNotification] = useState('');

    // --- FIX #2: A dedicated function for checking and showing notifications ---
    // This makes the logic much cleaner and easier to manage.
    const checkAndShowNotifications = (oldPet, newPet) => {
        const newHealthState = deriveHealthState(newPet);
        const oldHealthState = deriveHealthState(oldPet);

        // Check if the pet just became sick
        if (newHealthState === 'sick' && oldHealthState !== 'sick') {
            setNotification(`${newPet.name} is now SICK!`);
        }
        // Check if the pet just died
        else if (newHealthState === 'dead' && oldHealthState !== 'dead') {
            setNotification(`Oh no! ${newPet.name} has passed away.`);
        }
        // Check if the pet just became very hungry
        else if (newPet.levels.hungry > 80 && oldPet.levels.hungry <= 80) {
            setNotification(`${newPet.name} is very HUNGRY!`);
        }
        else if (newHealthState === 'fit' && oldHealthState !== 'fit') {
            setNotification(`${newPet.name} is FIT now!`);
        }
        else if (newHealthState === 'strong' && oldHealthState !== 'strong') {
            setNotification(`${newPet.name} is STRONG now!`);
        }
        else if (newHealthState === 'ok' && oldHealthState !== 'ok') {
            setNotification(`${newPet.name} is OK now!`);
        }
        else if (newHealthState === 'weak' && oldHealthState !== 'weak') {
            setNotification(`${newPet.name} is WEAK now!`);
        }

    };

    // This useEffect fetches the initial pet data
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
                const petWithState = { ...response.data, healthState: deriveHealthState(response.data) };
                setPet(petWithState);
            } catch (err) {
                setError('Could not find your pet.');
            } finally {
                setLoading(false);
            }
        };
        fetchPetDetails();
    }, [petId]);

    // This useEffect handles the real-time updates and triggers notifications
    useEffect(() => {
        if (user && token && pet) {
            const client = new Client({
                webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
                connectHeaders: { Authorization: `Bearer ${token}` },
                reconnectDelay: 5000,
            });

            client.onConnect = (frame) => {
                client.subscribe(`/user/${user.username}/queue/pet-updates`, (message) => {
                    const updatedPetFromWS = JSON.parse(message.body);

                    if (String(updatedPetFromWS.petId) === petId) {
                        // --- FIX #3: Use the new notification function ---
                        // It compares the current pet state ('pet') with the incoming one.
                        checkAndShowNotifications(pet, updatedPetFromWS);

                        // Update the pet state with the new data
                        const petWithDerivedState = { ...updatedPetFromWS, healthState: deriveHealthState(updatedPetFromWS) };
                        setPet(petWithDerivedState);
                    }
                });
            };

            client.activate();
            return () => { client.deactivate(); };
        }
    }, [user, token, pet, petId]);

    // This function gets called by PetActions after a user action
    const handlePetUpdate = (updatedPet) => {
        // Also check for notifications after a manual action
        checkAndShowNotifications(pet, updatedPet);
        const petWithDerivedState = { ...updatedPet, healthState: deriveHealthState(updatedPet) };
        setPet(petWithDerivedState);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Finding your pet...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;
    if (!pet) return <div className="text-center font-pixel text-2xl p-8">Pet not found.</div>;

    return (
        <>
            {notification && <Notification message={notification} onClose={() => setNotification('')} />}

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
                        <div className="w-full">
                            <PetImage pet={pet} />
                        </div>

                        <div className="space-y-8 w-full">
                            <PetStats pet={pet} />
                            <PetActions pet={pet} onAction={handlePetUpdate} />
                        </div>
                    </div>
                </main>
            </div>
        </>
    );
};

export default PetDetailPage;