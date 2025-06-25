import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/apiClient';
import PetImage from '../components/pets/PetImage';

const deriveHealthState = (pet) => {
  if (pet.evolutionState === 'DEAD') return 'dead';
  if (pet.healthLevel < 10) return 'sick';
  if (pet.healthLevel < 30) return 'weak';
  if (pet.healthLevel < 65) return 'ok';
  if (pet.healthLevel < 85) return 'fit';
  return 'strong';
};

const AdminPage = () => {
  const { user, logout } = useAuth();
  const [users, setUsers] = useState([]);
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchAdminData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const [usersResponse, petsResponse] = await Promise.all([
        apiClient.get('/admin/users/all'),
        apiClient.get('/admin/pets/all')
      ]);

      const petsWithDerivedState = petsResponse.data.map(pet => ({
        ...pet,
        healthState: deriveHealthState(pet)
      }));

      setUsers(usersResponse.data);
      setPets(petsWithDerivedState);

    } catch (err) {
      console.error("Failed to fetch admin data:", err);
      setError('Could not load admin data.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAdminData();
  }, [fetchAdminData]);

  const handleDeleteUser = async (userId, username) => {
    if (window.confirm(`Are you sure you want to delete ${username}? This will also delete their pets.`)) {
      try {
        await apiClient.delete(`/user/delete/${userId}`);
        setUsers(currentUsers => currentUsers.filter(user => user.id_user !== userId));
        setPets(currentPets => currentPets.filter(pet => pet.username !== username));
      } catch (err) {
        alert('Error: Could not delete the user.');
      }
    }
  };

  const handleDeletePet = async (petId) => {
    if (window.confirm(`Are you sure you want to delete this pet?`)) {
      try {
        await apiClient.delete(`pet/delete/${petId}`);
        setPets(currentPets => currentPets.filter(pet => pet.petId !== petId));
      } catch (err) {
        alert('Error: Could not delete the pet.');
      }
    }
  };

  return (
    <>
      <nav className="bg-white/70 backdrop-blur-md shadow-sm sticky top-0 z-40">
        <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex-shrink-0">
              <span className="font-bold">Hi, {user?.username} (Admin)</span>
            </div>
            <div className="flex items-center gap-4">
              <button onClick={fetchAdminData} className="btn btn-secondary">Refresh Data</button>
              <button onClick={logout} className="btn btn-danger">Logout</button>
            </div>
          </div>
        </div>
      </nav>

      <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="font-pixel text-4xl text-center mb-8">Admin Dashboard</h1>

        {loading ? (
          <div className="text-center font-pixel text-2xl p-8">Loading Admin Data...</div>
        ) : error ? (
          <div className="text-center font-pixel text-red-500 text-2xl p-8">{error}</div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">

            <div className="tamagotchi-container p-4">
              <h2 className="font-pixel text-3xl mb-4">Users ({users.length})</h2>
              <ul className="space-y-3">
                {users.map(user => {
                  const petCount = pets.filter(pet => pet.username === user.username).length;
                  return (
                    <li key={user.id_user} className="text-lg bg-gray-100 p-3 rounded flex justify-between items-center">
                      <div>
                        <span className="font-bold">{user.username}</span>
                        <span className="text-gray-600 ml-2">({petCount} {petCount === 1 ? 'pet' : 'pets'})</span>
                      </div>
                      <button onClick={() => handleDeleteUser(user.id_user, user.username)} className="btn btn-danger btn-sm">
                        Delete
                      </button>
                    </li>
                  );
                })}
              </ul>
            </div>

            <div className="tamagotchi-container p-4">
              <h2 className="font-pixel text-3xl mb-4">All Pets ({pets.length})</h2>
              <ul className="space-y-4">
                {pets.map(pet => (
                  <li key={pet.petId} className="bg-gray-100 p-3 rounded flex items-center gap-4">
                    <div className="w-40 h-40 flex-shrink-0">
                       <PetImage pet={pet} />
                    </div>
                    <div className="flex-grow">
                      <div className="flex justify-between items-start">
                        <div>
                          <h3 className="font-bold text-xl">{pet.name}</h3>
                          <p className="text-sm text-gray-600">Owner: {pet.username}</p>
                        </div>
                        <button onClick={() => handleDeletePet(pet.petId)} className="btn btn-danger btn-sm">
                          Delete
                        </button>
                      </div>
                      <div className="mt-2">
                        <p className="text-sm font-bold">Status: <span className="font-normal capitalize">{pet.healthState || 'OK'}</span></p>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default AdminPage;