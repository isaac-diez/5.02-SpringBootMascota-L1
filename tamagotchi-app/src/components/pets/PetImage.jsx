import React from 'react';

const PetImage = ({ pet }) => {
  // If there's no pet object, render an empty placeholder.
  if (!pet) {
    return <div className="w-full aspect-square bg-gray-200 rounded-md"></div>;
  }

  // --- THIS IS THE FIX ---
  // We now safely check if `pet.type` exists before trying to use it.
  // If it doesn't exist, we default to 'tamagotchi'.
  const petTypeFolder = (pet && pet.type)
    ? pet.type.toLowerCase()
    : 'tamagotchi';

  const validStates = ['strong', 'fit', 'ok', 'sick', 'dead'];
  let healthStateFile = pet.healthState ? pet.healthState.toLowerCase() : 'ok';
  if (!validStates.includes(healthStateFile)) {
    healthStateFile = 'ok';
  }

  const isSleeping = pet.sleeping && healthStateFile !== 'dead';
  const sleepingSuffix = isSleeping ? '-sleeping' : '';

  // Safely get the ID, defaulting to 1 if it's not present
  const petIdAsInt = parseInt(pet.petId, 10);
  const variant = !isNaN(petIdAsInt) ? (petIdAsInt % 3) + 1 : 1;

  const imagePath = `/images/${petTypeFolder}/${healthStateFile}${sleepingSuffix}-${variant}.jpg`;

  // Safely get the name for the alt text
  const altText = `${pet.name || 'Pet'}, who is ${isSleeping ? 'sleeping' : `in ${pet.healthState || 'ok'} health`}`;

  return (
    <div className="tamagotchi-container w-full aspect-square flex items-center justify-center bg-gray-100 overflow-hidden">
      <img
        src={imagePath}
        alt={altText}
        className="w-full h-full object-contain"
        onError={(e) => { e.target.onerror = null; e.target.src=`/images/tamagotchi/ok-1.jpg` }}
      />
    </div>
  );
};

export default PetImage;