import React, { useState } from 'react';
import apiClient from '../../api/apiClient';

const PetActions = ({ petId, onAction }) => {
  const [loadingAction, setLoadingAction] = useState(null); // To show loading state per button
  const [actionError, setActionError] = useState('');

  const handleAction = async (action) => {
    setLoadingAction(action); // e.g., 'feed', 'play'
    setActionError('');
    try {
      // This calls the correct endpoint, e.g., POST /pet/25/feed
      const response = await apiClient.post(`/pet/${petId}/${action}`);
      onAction(response.data); // Calls handlePetUpdate in the parent with the new pet data
    } catch (err) {
      console.error(`Error performing action '${action}':`, err);
      // Display specific error from backend if available, otherwise generic message
      setActionError(err.response?.data?.message || `Could not perform action: ${action}`);
    } finally {
      setLoadingAction(null); // Clear loading state
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
            disabled={!!loadingAction} // Disable all buttons while any action is in progress
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