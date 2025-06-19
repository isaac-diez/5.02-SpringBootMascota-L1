import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';

const CreatePetForm = ({ onPetCreated, onCancel }) => {
    const { createPet } = useAuth();
    const [name, setName] = useState(''); // FIX: Use `name` to match backend DTO
    const [type, setType] = useState('KAWAI'); // FIX: Use `type` and default to KAWAI
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!name.trim()) { setError('Pet name cannot be empty.'); return; }
        setIsSubmitting(true);
        setError('');
        try {
            // FIX: Send `name` and `type` to match the backend PetDto
            const newPet = await createPet({ name, type });
            onPetCreated(newPet);
        } catch (err) {
            setError('Could not create pet. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="tamagotchi-container w-[90%] md:w-1/2 lg:w-1/3 xl:w-1/4">
                <h2 className="font-pixel text-2xl mb-6 text-center">A new pet is born!</h2>
                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label htmlFor="petName" className="text-sm font-bold block text-left mb-1">Give it a name</label>
                        <input id="petName" type="text" value={name} onChange={(e) => setName(e.target.value)} required className="tamagotchi-input" />
                    </div>
                    <div>
                        <label htmlFor="petType" className="text-sm font-bold block text-left mb-1">Choose its type</label>
                        <select id="petType" value={type} onChange={(e) => setType(e.target.value)} className="tamagotchi-input">
                            <option value="KAWAI">Kawai Animal</option>
                            <option value="POKEMON">Collectible Monster</option>
                            <option value="TAMAGOTCHI">Digital Pet</option>
                        </select>
                    </div>
                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                    <div className="flex justify-end gap-4 pt-4">
                        <button type="button" onClick={onCancel} disabled={isSubmitting} className="btn bg-gray-300">Cancel</button>
                        <button type="submit" disabled={isSubmitting} className="btn btn-primary">{isSubmitting? 'Creating...' : 'Create!'}</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreatePetForm;