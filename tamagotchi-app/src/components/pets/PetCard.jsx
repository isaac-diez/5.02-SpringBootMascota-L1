import React from 'react';
import { Link } from 'react-router-dom';

const PetCard = ({ pet }) => {
    return (
        <Link to={`/pets/${pet.id}`} className="block group">
            <div className="bg-white p-4 border-4 border-black rounded-2xl transition-transform transform group-hover:scale-105 shadow-[8px_8px_0px_#000]">
                <div className="bg-pink-200 h-40 rounded-lg mb-4 border-2 border-black flex items-center justify-center">
                     <img src={`https://placehold.co/150x150/a3e635/222322?text=PET`} alt={pet.name} className="w-32 h-32 object-cover"/>
                </div>
                <h3 className="font-pixel text-lg text-center truncate">{pet.petName}</h3>
            </div>
        </Link>
    );
};

export default PetCard;