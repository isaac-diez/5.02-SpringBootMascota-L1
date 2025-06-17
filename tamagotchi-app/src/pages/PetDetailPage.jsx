import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import apiClient from '../api/apiClient';
import PetDisplay from '../components/pets/PetDisplay';
import PetStats from '../components/pets/PetStats';
import PetActions from '../components/pets/PetActions';

const PetDetailPage = () => {
    const { petId } = useParams();
    const [pet, setPet] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPetDetails = async () => {
            try {
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

    const handlePetUpdate = (updatedPet) => {
        setPet(updatedPet);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Finding your pet...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;
    if (!pet) return <div className="text-center font-pixel text-2xl p-8">Pet not found.</div>;

    return (
        <div className="w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

            <header className="flex justify-between items-center mb-8">

                <Link to="/pets" className="btn btn-secondary">{'<'} Back</Link>

                <h1 className="font-pixel text-3xl sm:text-4xl text-center">{pet.name}</h1>
                <div className="w-24"></div> {/* Spacer to balance header */}
            </header>
            <main>
                <div className="flex md:grid-cols-2 lg:grid-cols-2 gap-8 items-start">
                    {/* Left Column for the Image */}
                    <PetDisplay pet={pet} />
                    {/* Right Column for Stats and Actions */}
                    <div className="space-y-8">
                        <PetStats pet={pet} />
                        <PetActions petId={pet.petId} onAction={handlePetUpdate} />
                    </div>
                </div>
            </main>
        </div>
    );
};

export default PetDetailPage;