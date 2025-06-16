import React, { useState, useEffect, createContext, useContext, useMemo } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, Link, useParams } from 'react-router-dom';
import axios from 'axios';

// --- API SERVICE ---
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// --- AUTHENTICATION CONTEXT ---
const AuthContext = createContext(null);
const parseJwt = (token) => { try { return JSON.parse(atob(token.split('.')[1])); } catch (e) { return null; } };
const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('jwtToken'));
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      const decodedToken = parseJwt(token);
      if (decodedToken && decodedToken.exp * 1000 > Date.now()) {
        setUser({ username: decodedToken.sub, role: decodedToken.role });
      } else {
        localStorage.removeItem('jwtToken');
        setToken(null);
      }
    }
  }, [token]);

  const login = async (username, password) => {
    try {
      const response = await apiClient.post('/auth/login', { username, password });
      if (response.data && response.data.token) {
        const newToken = response.data.token;
        localStorage.setItem('jwtToken', newToken);
        setToken(newToken);
        navigate('/pets');
      }
    } catch (error) { console.error("Login failed:", error); throw error; }
  };

  const register = async (username, password) => {
    try { await apiClient.post('/auth/register', { username, password }); }
    catch (error) { console.error("Registration failed:", error); throw error; }
  };

  const createPet = async (petData) => {
    try {
        const response = await apiClient.post('/pet/new', petData);
        return response.data;
    } catch (error) { console.error("Error creating pet:", error); throw error; }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  const contextValue = useMemo(() => ({ token, user, isAuthenticated: !!token, login, logout, register, createPet }), [token, user]);
  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};
const useAuth = () => useContext(AuthContext);

// --- REUSABLE COMPONENTS ---
const AuthForm = ({ onSubmit, buttonText }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const handleSubmit = (e) => { e.preventDefault(); onSubmit({ username, password }); };
    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <div>
                <label htmlFor="username" className="text-sm font-bold block text-left mb-1">Username</label>
                <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} required className="tamagotchi-input" autoComplete="username" />
            </div>
            <div>
                <label htmlFor="password" className="text-sm font-bold block text-left mb-1">Password</label>
                <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required className="tamagotchi-input" autoComplete="current-password" />
            </div>
            {/* FIXED: Removed w-full and centered the button */}
            <div className="flex justify-center pt-2">
                 <button type="submit" className="btn btn-primary">{buttonText}</button>
            </div>
        </form>
    );
};

// --- PET COMPONENTS ---
const CreatePetForm = ({ onPetCreated, onCancel }) => {
    const { createPet } = useAuth();
    const [petName, setPetName] = useState('');
    const [petType, setPetType] = useState('TAMAGOTCHI');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!petName.trim()) { setError('Pet name cannot be empty.'); return; }
        setIsSubmitting(true);
        setError('');
        try {
            const newPet = await createPet({ petName, petType });
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
                        <input id="petName" type="text" value={petName} onChange={(e) => setPetName(e.target.value)} required className="tamagotchi-input" />
                    </div>
                    <div>
                        <label htmlFor="petType" className="text-sm font-bold block text-left mb-1">Choose its type</label>
                        <select id="petType" value={petType} onChange={(e) => setPetType(e.target.value)} className="tamagotchi-input">
                            <option value="TAMAGOTCHI">Tamagotchi (Retro)</option>
                            <option value="POKEMON">Pokemon (Anime)</option>
                            <option value="ANIMAL">Animal (Kawai)</option>
                        </select>
                    </div>
                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                    <div className="flex justify-end gap-4 pt-4">
                        <button type="button" onClick={onCancel} disabled={isSubmitting} className="btn bg-gray-300">Cancel</button>
                        <button type="submit" disabled={isSubmitting} className="btn btn-primary">{isSubmitting ? 'Creating...' : 'Create!'}</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const PetCard = ({ pet }) => {
    return (
        <Link to={`/pets/${pet.id}`} className="block group">
            <div className="bg-white p-4 border-4 border-black rounded-2xl transition-transform transform group-hover:scale-105 shadow-[8px_8px_0px_#000]">
                <div className="bg-pink-200 h-40 rounded-lg mb-4 border-2 border-black flex items-center justify-center">
                     <img src={`https://placehold.co/150x150/a3e635/222322?text=PET`} alt={pet.name} className="w-32 h-32 object-cover"/>
                </div>
                <h3 className="font-pixel text-lg text-center truncate">{pet.petName}</h3>
            </div>
        </Link>
    );
};

const PetDisplay = ({ pet }) => (
    <div className="tamagotchi-container flex justify-center items-center h-full">
        <img src={`https://placehold.co/400x400/e9d5ff/222322?text=${pet.name}`} alt={pet.name} className="w-full max-w-[300px] h-auto rounded-lg border-4 border-black"/>
    </div>
);

const PetStatBar = ({ label, value, colorClass }) => { const percentage = Math.max(0, Math.min(100, value)); return (<div className="mb-2"><div className="flex justify-between mb-1"><span className="text-base font-bold">{label}</span><span className="text-sm font-bold">{Math.round(percentage)}%</span></div><div className="w-full bg-gray-200 rounded-full h-5 border-2 border-black"><div className={`${colorClass} h-full rounded-full border-r-2 border-black`} style={{ width: `${percentage}%` }}></div></div></div>);};
const PetStats = ({ pet }) => { if (!pet || !pet.levels) return <div className="tamagotchi-container animate-pulse"><div className="h-8 bg-gray-300 rounded w-3/4 mx-auto"></div></div>; return (<div className="tamagotchi-container space-y-2"><h3 className="font-pixel text-xl text-center mb-4">State of {pet.name}</h3><PetStatBar label="Health" value={pet.levels.health} colorClass="bg-green-400" /><PetStatBar label="Happiness" value={pet.levels.happy} colorClass="bg-yellow-300" /><PetStatBar label="Hunger" value={pet.levels.hungry} colorClass="bg-orange-400" /><PetStatBar label="Energy" value={pet.levels.energy} colorClass="bg-blue-400" /><PetStatBar label="Hygiene" value={pet.levels.hygiene} colorClass="bg-teal-300" /></div>);};
const PetActions = ({ petId, onAction }) => { const [loadingAction, setLoadingAction] = useState(null); const handleAction = async (action) => { setLoadingAction(action); try { const response = await apiClient.post(`/pet/${petId}/${action}`); onAction(response.data); } catch (error) { console.error(`Error performing action ${action}:`, error); } finally { setLoadingAction(null); }}; return (<div className="tamagotchi-container"><h3 className="font-pixel text-xl text-center mb-4">Actions</h3><div className="grid grid-cols-2 gap-4"><button onClick={() => handleAction('feed')} disabled={!!loadingAction} className="btn bg-green-400 w-full">{loadingAction === 'feed' ? '...' : 'Feed'}</button><button onClick={() => handleAction('play')} disabled={!!loadingAction} className="btn bg-yellow-300 w-full">{loadingAction === 'play' ? '...' : 'Play'}</button><button onClick={() => handleAction('sleep')} disabled={!!loadingAction} className="btn bg-blue-400 w-full">{loadingAction === 'sleep' ? '...' : 'Sleep'}</button><button onClick={() => handleAction('meds')} disabled={!!loadingAction} className="btn bg-purple-400 w-full">{loadingAction === 'meds' ? '...' : 'Meds'}</button><button onClick={() => handleAction('clean')} disabled={!!loadingAction} className="btn bg-teal-300 col-span-2 w-full">{loadingAction === 'clean' ? '...' : 'Clean'}</button></div></div>);};

// --- PAGES ---
const LoginPage = () => {
    const { login } = useAuth();
    const [error, setError] = useState('');
    const handleLogin = async (credentials) => {
        try { await login(credentials.username, credentials.password); setError(''); }
        catch (err) { setError('Login failed. Check your credentials.'); }
    };
    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            {/* FIXED: Using max-w-sm for a responsive, centered container */}
            <div className="tamagotchi-container w-full max-w-sm">
                <h1 className="font-pixel text-3xl text-center mb-2">Login</h1>
                <p className="text-center text-gray-600 mb-6">Welcome back!</p>
                <AuthForm onSubmit={handleLogin} buttonText="Login"/>
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6">
                    <p className="text-gray-600">No account? <Link to="/register" className="font-bold text-[var(--tam-pink)] hover:underline">Register</Link></p>
                </div>
            </div>
        </div>
    );
};

const RegisterPage = () => {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const handleRegister = async (credentials) => {
        try { await register(credentials.username, credentials.password); setError(''); navigate('/login?registered=true'); }
        catch (err) { setError('Registration failed.'); }
    };
    return (
        <div className="min-h-screen flex items-center justify-center p-4">
             {/* FIXED: Using max-w-sm for a responsive, centered container */}
            <div className="tamagotchi-container w-full max-w-sm">
                <h1 className="font-pixel text-3xl text-center mb-2">Register</h1>
                <p className="text-center text-gray-600 mb-6">Create your account!</p>
                <AuthForm onSubmit={handleRegister} buttonText="Register"/>
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6">
                    <p className="text-gray-600">Have an account? <Link to="/login" className="font-bold text-[var(--tam-pink)] hover:underline">Login</Link></p>
                </div>
            </div>
        </div>
    );
};

const ProtectedRoute = ({ children }) => { const { isAuthenticated } = useAuth(); return isAuthenticated ? children : <Navigate to="/login" replace />; };

const PetListPage = () => {
    const { user, logout } = useAuth();
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isCreating, setIsCreating] = useState(false);

    useEffect(() => {
        const fetchPets = async () => {
            try { setLoading(true); const response = await apiClient.get('/pet/my-pets'); setPets(response.data || []); }
            catch (err) { console.error("Error fetching pets:", err); setError('Could not load your pets.'); }
            finally { setLoading(false); }
        };
        fetchPets();
    }, []);

    const handlePetCreated = (newPet) => {
        setPets(currentPets => [...currentPets, newPet]);
        setIsCreating(false);
    };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Loading...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;

    return (
        <>
            <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <header className="flex flex-col sm:flex-row justify-between items-center mb-8 gap-4">
                    <h1 className="font-pixel text-3xl sm:text-4xl">My Pets</h1>
                    <div className="flex items-center gap-4">
                        <span className="font-bold hidden sm:inline">Hi, {user?.username}</span>
                        <button onClick={() => setIsCreating(true)} className="btn btn-primary">New Pet</button>
                        <button onClick={logout} className="btn btn-danger">Logout</button>
                    </div>
                </header>
                <main>
                    {pets.length === 0 ? (
                         <div className="text-center mt-20 tamagotchi-container max-w-md mx-auto">
                            <h2 className="font-pixel text-2xl mb-4">No pets yet!</h2>
                            <p className="mb-6">Click "New Pet" to create your first digital friend.</p>
                         </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                            {pets.map(pet => (<PetCard key={pet.id} pet={pet} />))}
                        </div>
                    )}
                </main>
            </div>
            {isCreating && <CreatePetForm onPetCreated={handlePetCreated} onCancel={() => setIsCreating(false)} />}
        </>
    );
};

const PetDetailPage = () => {
    const { petId } = useParams();
    const [pet, setPet] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    useEffect(() => { const fetchPetDetails = async () => { try { const response = await apiClient.get(`/pet/get/${petId}`); setPet(response.data); } catch (err) { setError('Could not find your pet.'); } finally { setLoading(false); }}; fetchPetDetails(); }, [petId]);
    const handlePetUpdate = (updatedPet) => { setPet(updatedPet); };

    if (loading) return <div className="text-center font-pixel text-2xl p-8">Finding your pet...</div>;
    if (error) return <div className="text-center text-red-500 font-pixel text-2xl p-8">{error}</div>;
    if (!pet) return <div className="text-center font-pixel text-2xl p-8">Pet not found.</div>;

    return (
        <div className="w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <header className="flex justify-between items-center mb-8">
                <Link to="/pets" className="btn btn-secondary">{'<'} Back</Link>
                <h1 className="font-pixel text-3xl sm:text-4xl text-center">{pet.name}</h1>
                <div className="w-24"></div> {/* Spacer */}
            </header>
            <main>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    <PetDisplay pet={pet} />
                    <div className="space-y-8">
                        <PetStats pet={pet} />
                        <PetActions petId={pet.petId} onAction={handlePetUpdate} />
                    </div>
                </div>
            </main>
        </div>
    );
};

// --- APP ROUTER ---
function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/pets" element={<ProtectedRoute><PetListPage /></ProtectedRoute>} />
            <Route path="/pets/:petId" element={<ProtectedRoute><PetDetailPage /></ProtectedRoute>} />
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
