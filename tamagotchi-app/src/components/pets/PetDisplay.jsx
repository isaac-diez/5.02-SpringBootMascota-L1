import React from 'react';
import PetImage from './PetImage';

const PetDisplay = ({ pet }) => {
  if (!pet) return null;

  return (
    <div className="w-full">
      <PetImage pet={pet} />
    </div>
  );
};

export default PetDisplay;