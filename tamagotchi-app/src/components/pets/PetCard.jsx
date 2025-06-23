import React from 'react';
import { Link } from 'react-router-dom';
import PetImage from './PetImage';

const PetCard = ({ pet }) => {
  if (!pet || !pet.petId) {
    return null;
  }

  return (
    <Link to={`/pets/${pet.petId}`} className="block tamagotchi-container text-center hover:scale-105 transition-transform duration-200">
      <div className="p-4">
        <PetImage pet={pet} />
        <h3 className="font-pixel text-2xl mt-4 truncate">{pet.name}</h3>
        <p className="text-sm uppercase text-gray-500">{pet.sleeping ? 'Sleeping' : pet.healthState}</p>
      </div>
    </Link>
  );
};

export default PetCard;