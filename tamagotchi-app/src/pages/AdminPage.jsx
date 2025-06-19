import React, { useState, useEffect } from 'react';
import apiClient from '../api/apiClient'; // Adjust path to your apiClient if needed

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAdminData = async () => {
      try {
        setLoading(true);
        setError('');

        // FIX: Using the correct API endpoints as you specified.
        // Promise.all fetches both sets of data in parallel for efficiency.
        const [usersResponse, petsResponse] = await Promise.all([
          apiClient.get('/user/getAll'),
          apiClient.get('/pet/getAll')
        ]);

        setUsers(usersResponse.data);
        setPets(petsResponse.data);

      } catch (err) {
        console.error("Failed to fetch admin data:", err);
        // Set a user-friendly error message
        setError('Could not load admin data. You may not have permission or the server may be down.');
      } finally {
        setLoading(false);
      }
    };

    fetchAdminData();
  }, []); // The empty dependency array ensures this runs only once when the component mounts.

  if (loading) {
    return <div className="text-center font-pixel text-2xl p-8">Loading Admin Data...</div>;
  }

  if (error) {
    return <div className="text-center font-pixel text-red-500 text-2xl p-8">{error}</div>;
  }

  return (
    <div className="container mx-auto p-4 sm:p-6 lg:p-8">
      <h1 className="font-pixel text-4xl text-center mb-8">Admin Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">

        {/* Users List */}
        <div className="tamagotchi-container p-4">
          <h2 className="font-pixel text-3xl mb-4">Users ({users.length})</h2>
          <ul className="space-y-2">
            {users.map(user => (
              <li key={user.id} className="text-lg bg-gray-100 p-2 rounded">
                {user.username} - (ID: {user.id})
              </li>
            ))}
          </ul>
        </div>

        {/* Pets List */}
        <div className="tamagotchi-container p-4">
          <h2 className="font-pixel text-3xl mb-4">Pets ({pets.length})</h2>
          <ul className="space-y-2">
            {pets.map(pet => (
              <li key={pet.id} className="text-lg bg-gray-100 p-2 rounded">
                {pet.name} - (Owner ID: {pet.userId})
              </li>
            ))}
          </ul>
        </div>

      </div>
    </div>
  );
};

export default AdminPage;