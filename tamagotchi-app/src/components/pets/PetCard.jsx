import React from 'react';
import { Link } from 'react-router-dom';
import PetImage from './PetImage'; // <-- Import the new component

const PetCard = ({ pet }) => {
  return (
    <Link to={`/pets/${pet.id}`} className="block tamagotchi-container text-center hover:scale-105 transition-transform duration-200">
      <div className="p-4">
        {/* Replace your old image logic with this one line */}
        <PetImage pet={pet} />

        <h3 className="font-pixel text-2xl mt-4 truncate">{pet.petName}</h3>
        <p className="text-sm uppercase text-gray-500">{pet.sleeping ? 'Sleeping' : pet.healthState}</p>
      </div>
    </Link>
  );
};

export default PetCard;