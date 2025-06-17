import React, { useState } from 'react';
import apiClient from '../../api/apiClient';

const PetActions = ({ petId, onAction }) => {
    const [loadingAction, setLoadingAction] = useState(null);

    const actionColors = {
        feed: '#4ade80',  // green-400
        play: '#fde047',  // yellow-300
        sleep: '#60a5fa', // blue-400
        meds: '#c084fc',  // purple-400
        clean: '#5eead4'  // teal-300
    };

    const handleAction = async (action) => {
        setLoadingAction(action);
        try {
            const response = await apiClient.post(`/pet/${petId}/${action}`);
            onAction(response.data);
        } catch (error) {
            console.error(`Error performing action ${action}:`, error);
        } finally {
            setLoadingAction(null);
        }
    };

    return (
        <div className="tamagotchi-container-detail">
            <h3 className="font-pixel text-xl text-center mb-4">Actions</h3>
            <div className="grid grid-cols-2 gap-4">
                <button onClick={() => handleAction('feed')} disabled={!!loadingAction} className="btn w-full" style={{backgroundColor: actionColors.feed}}>{loadingAction === 'feed'? '...' : 'Feed'}</button>
                <button onClick={() => handleAction('play')} disabled={!!loadingAction} className="btn w-full" style={{backgroundColor: actionColors.play}}>{loadingAction === 'play'? '...' : 'Play'}</button>
                <button onClick={() => handleAction('sleep')} disabled={!!loadingAction} className="btn w-full" style={{backgroundColor: actionColors.sleep}}>{loadingAction === 'sleep'? '...' : 'Sleep'}</button>
                <button onClick={() => handleAction('meds')} disabled={!!loadingAction} className="btn w-full" style={{backgroundColor: actionColors.meds}}>{loadingAction === 'meds'? '...' : 'Meds'}</button>
                <button onClick={() => handleAction('clean')} disabled={!!loadingAction} className="btn col-span-2 w-full" style={{backgroundColor: actionColors.clean}}>{loadingAction === 'clean'? '...' : 'Clean'}</button>
            </div>
        </div>
    );
};

export default PetActions;