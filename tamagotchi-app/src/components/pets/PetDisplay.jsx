import React from 'react';
import PetImage from './PetImage'; // <-- Import the new component

const PetDisplay = ({ pet }) => (
    <div className="tamagotchi-container grid justify-center items-center">
                <PetImage pet={pet} alt={pet.name} className="w-full max-w-[300px] h-auto rounded-lg border-4 border-black"/>
    </div>
);

export default PetDisplay;