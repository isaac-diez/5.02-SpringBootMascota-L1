import React, { useState } from 'react';
import apiClient from '../../api/apiClient';

const PetActions = ({ pet, onAction }) => {
  const [loadingAction, setLoadingAction] = useState(null);
  const [actionError, setActionError] = useState('');
  const isDead = pet.evolutionState === 'DEAD' || pet.healthState === 'DEAD';

  const handleAction = async (action) => {
    setLoadingAction(action);
    setActionError('');
    try {

      const response = await apiClient.post(`/pet/${pet.petId}/${action}`);
      onAction(response.data);
    } catch (err) {
      console.error(`Error performing action '${action}':`, err);

      setActionError(err.response?.data?.message || `Could not perform action: ${action}`);
    } finally {
      setLoadingAction(null);
    }
  };

  const actions = ['feed', 'play', 'sleep', 'clean', 'meds'];

  return (
    <div className="tamagotchi-container p-6">
      <h3 className="font-pixel text-xl mb-4 text-center">Actions</h3>
      {actionError && <p className="text-red-500 text-sm text-center mb-4">{actionError}</p>}
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
        {actions.map((action) => (
          <button
            key={action}
            onClick={() => handleAction(action)}
            disabled={!!loadingAction || isDead}
            className={`btn capitalize ${loadingAction === action ? 'opacity-50 cursor-not-allowed' : ''}`}
          >
            {loadingAction === action ? '...' : action}
          </button>
        ))}
      </div>
    </div>
  );
};

export default PetActions;