import React from 'react';

const PetImage = ({ pet }) => {
  // If there's no pet object, render an empty placeholder.
  if (!pet) {
    return <div className="w-full aspect-square bg-gray-200 rounded-md"></div>;
  }

  const petTypeFolder = (pet && pet.type)
    ? pet.type.toLowerCase()
    : 'tamagotchi';

  const validStates = ['strong', 'fit', 'ok', 'weak', 'sick', 'dead'];
  let healthStateFile = pet.healthState ? pet.healthState.toLowerCase() : 'ok';
  if (!validStates.includes(healthStateFile)) {
    healthStateFile = 'ok';
  }

  const isSleeping = pet.sleeping && healthStateFile !== 'dead';
  const sleepingSuffix = isSleeping ? '-sleeping' : '';

  const petIdAsInt = parseInt(pet.petId, 10);
  const variant = !isNaN(petIdAsInt) ? (petIdAsInt % 3) + 1 : 1;

  const imagePath = `/images/${petTypeFolder}/${healthStateFile}${sleepingSuffix}-${variant}.jpg`;

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