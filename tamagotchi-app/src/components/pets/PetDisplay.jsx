import React from 'react';

const PetDisplay = ({ pet }) => (
    <div className="tamagotchi-container-detail flex justify-center items-center">
        <img src={`https://placehold.co/400x400/e9d5ff/222322?text=${pet.name}`} alt={pet.name} className="w-full max-w-[300px] h-auto rounded-lg border-4 border-black"/>
    </div>
);

export default PetDisplay;