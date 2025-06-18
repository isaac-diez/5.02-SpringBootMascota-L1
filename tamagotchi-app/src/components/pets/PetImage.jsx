import React from 'react';

// This component now only needs pet.id, pet.petType, pet.healthState, and pet.sleeping
const PetImage = ({ pet }) => {
  const validStates = ['strong', 'fit', 'ok', 'sick', 'dead'];
  let healthStateFile = pet.healthState ? pet.healthState.toLowerCase() : 'ok';
  if (!validStates.includes(healthStateFile)) {
    healthStateFile = 'ok';
  }

  const isSleeping = pet.sleeping && healthStateFile !== 'dead';
  const sleepingSuffix = isSleeping ? '-sleeping' : '';

  // --- THIS IS THE NEW CORE LOGIC ---
  // We derive a consistent variant number (1, 2, or 3) from the pet's ID.
  // The modulo operator (%) gives us a predictable number for any given ID.
  // This ensures the pet's appearance is always the same, but different from other pets.
  const petIdAsInt = parseInt(pet.id, 10);
  const variant = !isNaN(petIdAsInt) ? (petIdAsInt % 3) + 1 : 1;
  // ------------------------------------

  // Construct the final image path including the calculated variant number
  const petTypeFolder = pet.petType ? pet.petType.toLowerCase() : 'tamagotchi';
  const imagePath = `/images/${petTypeFolder}/${healthStateFile}${sleepingSuffix}-${variant}.jpg`;

  const altText = `${pet.petName}, who is ${isSleeping ? 'sleeping' : `in ${pet.healthState} health`}`;

  return (
    <div className="w-full aspect-square flex items-center justify-center bg-gray-100 rounded-md overflow-hidden">
      <img
        src={imagePath}
        alt={altText}
        className="w-full h-full object-contain"
        // Fallback in case a specific variant image is missing
        onError={(e) => { e.target.onerror = null; e.target.src=`/images/${petTypeFolder}/ok-1.jpg` }}
      />
    </div>
  );
};

export default PetImage;