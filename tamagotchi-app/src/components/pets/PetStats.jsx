import React from 'react';
import PetStatBar from './PetStatsBar.jsx';

const PetStats = ({ pet }) => {
    if (!pet ||!pet.levels) {
        return (
            <div className="tamagotchi-container animate-pulse">
                <div className="h-8 bg-gray-300 rounded w-3/4 mx-auto"></div>
            </div>
        );
    }

    return (
        <div className="tamagotchi-container">
            <h3 className="font-pixel text-xl text-center mb-4">State of {pet.name}</h3>
            <PetStatBar label="Health"    value={pet.levels.health}  hexColor="#4ade80" />
            <PetStatBar label="Happiness" value={pet.levels.happy}   hexColor="#fde047" />
            <PetStatBar label="Hunger"    value={pet.levels.hungry}  hexColor="#fb923c" />
            <PetStatBar label="Energy"    value={pet.levels.energy}  hexColor="#60a5fa" />
            <PetStatBar label="Hygiene"   value={pet.levels.hygiene} hexColor="#5eead4" />
        </div>
    );
};

export default PetStats;