import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import apiClient from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import PetDisplay from '../components/pets/PetDisplay';
import PetStats from '../components/pets/PetStats';
import PetActions from '../components/pets/PetActions';

const PetDetailPage = () => {
    const { petId } = useParams();
    const { logout } = useAuth(); // Assuming useAuth provides logout
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

    // This function will be called by PetActions to update the pet state
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
                {/* FIX: This class now creates a responsive 2-column grid correctly */}
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