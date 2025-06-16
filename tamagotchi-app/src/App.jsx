import React, { useState, useEffect, createContext, useContext, useMemo } from 'react';
// Importa los componentes de React Router, incluyendo `useParams` para leer IDs de la URL
import { BrowserRouter, Routes, Route, Navigate, useNavigate, Link, useParams } from 'react-router-dom';
// Importa Axios para las llamadas a la API
import axios from 'axios';

// --- ESTILO Y FUENTES ---
// Importamos una fuente de Google Fonts para la estética retro.
// Asegúrate de añadir el @import en tu fichero `src/index.css`.
/* @import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');
*/


// --- 1. SERVICIO DE API (services/api.js) ---
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


// --- 2. CONTEXTO DE AUTENTICACIÓN (context/AuthContext.js) ---
const AuthContext = createContext(null);

const parseJwt = (token) => {
  try { return JSON.parse(atob(token.split('.')[1])); }
  catch (e) { return null; }
};

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
    } catch (error) {
      console.error("Fallo en el login:", error);
      throw error;
    }
  };

  const register = async (username, password) => {
    try {
      await apiClient.post('/auth/register', { username, password });
    } catch (error) {
      console.error("Fallo en el registro:", error);
      throw error;
    }
  };

  const createPet = async (petData) => {
    try {
        const response = await apiClient.post('/pet/new', petData);
        return response.data;
    } catch (error) {
        console.error("Error al crear la mascota:", error);
        throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  const contextValue = useMemo(
    () => ({ token, user, isAuthenticated: !!token, login, logout, register, createPet }),
    [token, user]
  );

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

const useAuth = () => useContext(AuthContext);


// --- 3. PÁGINAS Y COMPONENTES (pages/ y components/) ---

// -- Componente reutilizable para formularios de autenticación --
const AuthForm = ({ onSubmit, buttonText, isLogin = false }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit({ username, password });
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <div>
                <label htmlFor="username" className="text-sm font-bold text-gray-400 block">Nombre de Usuario</label>
                <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} required className="w-full p-3 mt-2 text-gray-300 bg-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 transition" autoComplete="username" />
            </div>
            <div>
                <label htmlFor="password" className="text-sm font-bold text-gray-400 block">Contraseña</label>
                <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required className="w-full p-3 mt-2 text-gray-300 bg-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 transition" autoComplete={isLogin ? "current-password" : "new-password"} />
            </div>
            <button type="submit" className="w-full py-3 px-4 bg-indigo-600 hover:bg-indigo-700 rounded-md text-white font-semibold transition duration-200">
                {buttonText}
            </button>
        </form>
    );
};

// -- Páginas de Login y Registro (sin cambios significativos) --
const LoginPage = () => {
    const { login } = useAuth();
    const [error, setError] = useState('');
    const handleLogin = async (credentials) => {
        try { await login(credentials.username, credentials.password); setError(''); }
        catch (err) { setError('Error al iniciar sesión. Verifica tus credenciales.'); }
    };
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white p-4">
            <div className="w-full max-w-md p-8 space-y-8 bg-gray-800 rounded-xl shadow-lg">
                <h1 className="text-3xl font-bold text-center">Bienvenido de Vuelta</h1>
                <p className="text-center text-gray-400">Introduce tus credenciales para continuar</p>
                <AuthForm onSubmit={handleLogin} buttonText="Entrar" isLogin={true} />
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6"><p className="text-gray-400">¿No tienes una cuenta?</p><Link to="/register" className="font-medium text-indigo-400 hover:text-indigo-300">Regístrate aquí</Link></div>
            </div>
        </div>
    );
};

const RegisterPage = () => {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const handleRegister = async (credentials) => {
        if (credentials.password.length < 4) { setError('La contraseña debe tener al menos 4 caracteres.'); return; }
        try { await register(credentials.username, credentials.password); setError(''); navigate('/login?registered=true'); }
        catch (err) { setError('Error en el registro. El usuario puede que ya exista.'); }
    };
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white p-4">
            <div className="w-full max-w-md p-8 space-y-8 bg-gray-800 rounded-xl shadow-lg">
                <h1 className="text-3xl font-bold text-center">Crea tu Cuenta</h1>
                <p className="text-center text-gray-400">Únete a la aventura Tamagotchi</p>
                <AuthForm onSubmit={handleRegister} buttonText="Registrarse" />
                {error && <p className="text-red-500 text-center mt-4">{error}</p>}
                <div className="text-center mt-6"><p className="text-gray-400">¿Ya tienes una cuenta?</p><Link to="/login" className="font-medium text-indigo-400 hover:text-indigo-300">Inicia sesión</Link></div>
            </div>
        </div>
    );
};

// -- Componente para proteger rutas --
const ProtectedRoute = ({ children }) => {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? children : <Navigate to="/login" replace />;
};


// --- COMPONENTES Y PÁGINAS PARA LAS MASCOTAS ---

// -- Formulario para crear una nueva mascota --
const CreatePetForm = ({ onPetCreated, onCancel }) => {
    const { createPet } = useAuth();
    const [petName, setPetName] = useState('');
    const [petType, setPetType] = useState('TAMAGOTCHI');
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!petName.trim()) {
            setError('El nombre de la mascota no puede estar vacío.');
            return;
        }
        setIsSubmitting(true);
        setError('');
        try {
            const newPet = await createPet({ petName, petType });
            onPetCreated(newPet);
        } catch (err) {
            setError('No se pudo crear la mascota. Inténtalo de nuevo.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-gray-800 p-8 rounded-xl shadow-2xl w-full max-w-lg text-white animate-fade-in">
                <h2 className="text-2xl font-bold mb-6 text-center">¡Nace una nueva mascota!</h2>
                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label htmlFor="petName" className="text-sm font-bold text-gray-400 block">Dale un nombre</label>
                        <input id="petName" type="text" value={petName} onChange={(e) => setPetName(e.target.value)} required className="w-full p-3 mt-2 text-gray-300 bg-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 transition" />
                    </div>
                    <div>
                        <label htmlFor="petType" className="text-sm font-bold text-gray-400 block">Elige su tipo</label>
                        <select id="petType" value={petType} onChange={(e) => setPetType(e.target.value)} className="w-full p-3 mt-2 text-gray-300 bg-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500 transition appearance-none">
                            <option value="TAMAGOTCHI">Tamagotchi (Retro)</option>
                            <option value="POKEMON">Pokemon (Dibujo)</option>
                            <option value="ANIMAL">Animal (Kawai)</option>
                        </select>
                    </div>
                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                    <div className="flex justify-end gap-4 pt-4">
                        <button type="button" onClick={onCancel} disabled={isSubmitting} className="py-2 px-6 bg-gray-600 hover:bg-gray-500 rounded-md font-semibold transition">Cancelar</button>
                        <button type="submit" disabled={isSubmitting} className="py-2 px-6 bg-indigo-600 hover:bg-indigo-700 rounded-md font-semibold transition">
                           {isSubmitting ? 'Creando...' : '¡Crear!'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};


// -- Componente para una tarjeta de mascota individual --
const PetCard = ({ pet }) => {
    const petStyles = {
        TAMAGOTCHI: { image: "https://placehold.co/200x200/a3e635/172554?text=8-bit", borderColor: "border-lime-400", font: "font-press-start" },
        POKEMON: { image: "https://placehold.co/200x200/f87171/4c0519?text=Poke", borderColor: "border-red-500", font: "" },
        ANIMAL: { image: "https://placehold.co/200x200/60a5fa/0c2f53?text=Kawai", borderColor: "border-blue-400", font: "" }
    };
    const style = petStyles[pet.petType] || petStyles.ANIMAL;

    return (
        <Link to={`/pets/${pet.id}`} className="block group">
            <div className={`bg-gray-800 p-4 border-4 ${style.borderColor} rounded-lg transition-transform transform group-hover:scale-105 group-hover:shadow-2xl group-hover:shadow-indigo-500/50`}>
                <img src={style.image} alt={pet.petName} className="w-full h-40 object-cover rounded-md mb-4" />
                <h3 className={`text-xl text-center font-bold truncate ${style.font}`}>{pet.petName}</h3>
            </div>
        </Link>
    );
}

// -- Página que muestra la lista de mascotas del usuario --
const PetListPage = () => {
    const { user, logout } = useAuth();
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isCreating, setIsCreating] = useState(false);

    const fetchPets = async () => {
        try {
            setLoading(true);
            const response = await apiClient.get('/pet/my-pets');
            // La API devuelve 204 si no hay mascotas, lo que axios trata como éxito sin datos.
            setPets(response.data || []);
        } catch (err) {
            console.error("Error al obtener las mascotas:", err);
            setError('No se pudieron cargar tus mascotas. Inténtalo de nuevo más tarde.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchPets(); }, []);

    const handlePetCreated = (newPet) => {
        setPets(currentPets => [...currentPets, newPet]);
        setIsCreating(false);
    };

    if (loading) return <div className="text-center text-white text-2xl p-8">Cargando tus mascotas...</div>;
    if (error) return <div className="text-center text-red-500 text-2xl p-8">{error}</div>;

    return (
        <>
            <div className="p-4 sm:p-8 text-white min-h-screen">
                <header className="flex justify-between items-center mb-8">
                    <h1 className="text-2xl sm:text-4xl font-bold">Mis Mascotas</h1>
                    <div>
                        <span className="mr-4 text-gray-400 hidden sm:inline">Hola, {user?.username}</span>
                        <button onClick={logout} className="py-2 px-4 bg-red-600 hover:bg-red-700 rounded-md text-sm sm:text-base">Cerrar Sesión</button>
                    </div>
                </header>

                {pets.length === 0 ? (
                     <div className="text-center text-gray-400 mt-20">
                        <p className="text-2xl">¡Aún no tienes mascotas!</p>
                        <button onClick={() => setIsCreating(true)} className="mt-4 py-3 px-6 bg-indigo-600 hover:bg-indigo-700 rounded-md font-bold transition">¡Crea tu primera mascota!</button>
                     </div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                        {pets.map(pet => (
                            <PetCard key={pet.id} pet={pet} />
                        ))}
                    </div>
                )}
            </div>

            {isCreating && <CreatePetForm onPetCreated={handlePetCreated} onCancel={() => setIsCreating(false)} />}
        </>
    );
};


// -- NUEVOS COMPONENTES PARA LA PÁGINA DE DETALLE --

const PetDisplay = ({ pet }) => {
    // Mapeo de estados a imágenes. Reemplaza las URLs con las rutas a tus assets.
    const imageMap = {
        // Imágenes por defecto para cada tipo
        TAMAGOTCHI: "https://placehold.co/300x300/a3e635/172554?text=Tamagotchi",
        POKEMON: "https://placehold.co/300x300/f87171/4c0519?text=Pokemon",
        ANIMAL: "https://placehold.co/300x300/60a5fa/0c2f53?text=Animal",
        // Imágenes específicas por estado
        SICK: "https://placehold.co/300x300/a1a1aa/44403c?text=Enfermo",
        DEAD: "https://placehold.co/300x300/a1a1aa/44403c?text=RIP",
        SAD: "https://placehold.co/300x300/a16207/422006?text=Triste",
        MISERABLE: "https://placehold.co/300x300/a16207/422006?text=Muy+Triste",
        HAPPY: "https://placehold.co/300x300/fde047/854d0e?text=Feliz",
        EXULTANT: "https://placehold.co/300x300/fde047/854d0e?text=MUY+Feliz",
        // ... puedes añadir más para sleeping, etc.
    };

    // Lógica para decidir qué imagen mostrar
    const getPetImage = () => {
        if (pet.healthState === 'SICK') return imageMap.SICK;
        if (pet.healthState === 'DEAD') return imageMap.DEAD;
        if (pet.happinessState === 'MISERABLE') return imageMap.MISERABLE;
        if (pet.happinessState === 'SAD') return imageMap.SAD;
        if (pet.happinessState === 'EXULTANT') return imageMap.EXULTANT;
        if (pet.happinessState === 'HAPPY') return imageMap.HAPPY;
        // Si no hay un estado especial, muestra la imagen por defecto de su tipo
        return imageMap[pet.petType] || imageMap.ANIMAL;
    };

    return (
        <div className="flex justify-center items-center bg-gray-800 p-4 rounded-lg">
             <img
                src={getPetImage()}
                alt={pet.petName}
                className="max-w-xs w-full h-auto"
             />
        </div>
    );
};

const PetStatBar = ({ label, value, colorClass }) => {
    const percentage = Math.max(0, Math.min(100, value)); // Ensure value is between 0 and 100
    return (
        <div>
            <div className="flex justify-between mb-1">
                <span className="text-base font-medium text-gray-300">{label}</span>
                <span className="text-sm font-medium text-gray-300">{Math.round(percentage)}%</span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-4">
                <div
                    className={`${colorClass} h-4 rounded-full`}
                    style={{ width: `${percentage}%` }}
                ></div>
            </div>
        </div>
    );
};


const PetStats = ({ pet }) => {
    // --- DEBUGGING STEP ---
    // This log will show you exactly what data the component is receiving and when.
    // Open your browser's developer console (F12) to see the output.
    console.log('PetStats component received this pet data:', pet);

    // --- THE FIX (GUARD CLAUSE) ---
    // If 'pet' or 'pet.levels' doesn't exist yet, we stop here and show a loading state.
    // This prevents the code below from running with incomplete data and causing errors.
    if (!pet || !pet.levels) {
        return (
            <div className="bg-gray-800 p-6 rounded-lg space-y-4 animate-pulse">
                <h3 className="text-xl font-bold text-center mb-4 text-gray-400">Loading Stats...</h3>
                <div className="h-4 bg-gray-700 rounded-full w-full mb-2.5"></div>
                <div className="h-4 bg-gray-700 rounded-full w-full mb-2.5"></div>
                <div className="h-4 bg-gray-700 rounded-full w-full mb-2.5"></div>
                <div className="h-4 bg-gray-700 rounded-full w-full mb-2.5"></div>
                <div className="h-4 bg-gray-700 rounded-full w-full mb-2.5"></div>
            </div>
        );
    }

    // --- YOUR ORIGINAL CODE ---
    // This part will now only run when the data is fully loaded.
    return (
        <div className="bg-gray-800 p-6 rounded-lg space-y-4">
            <h3 className="text-xl font-bold text-center mb-4">State of {pet.name}</h3>
            <PetStatBar label="Health" value={pet.levels.health} colorClass="bg-green-500" />
            <PetStatBar label="Happiness" value={pet.levels.happy} colorClass="bg-yellow-400" />
            <PetStatBar label="Hungry" value={pet.levels.hungry} colorClass="bg-orange-500" />
            <PetStatBar label="Energy" value={pet.levels.energy} colorClass="bg-blue-500" />
            <PetStatBar label="Hygiene" value={pet.levels.hygiene} colorClass="bg-cyan-400" />
        </div>
    );
};

const PetActions = ({ petId, onAction }) => {
    const [loadingAction, setLoadingAction] = useState(null);

    const handleAction = async (action) => {
        setLoadingAction(action);
        try {
            // Llama al endpoint correspondiente en el backend
            const response = await apiClient.post(`/pet/${petId}/${action}`);
            onAction(response.data); // Llama al callback con la mascota actualizada
        } catch (error) {
            console.error(`Error al realizar la acción ${action}:`, error);
            // Podrías mostrar un mensaje de error al usuario aquí
        } finally {
            setLoadingAction(null);
        }
    };

    return (
        <div className="bg-gray-800 p-6 rounded-lg">
             <h3 className="text-xl font-bold text-center mb-4">¿What do you want to do?</h3>
             <div className="grid grid-cols-2 gap-4">
                <button onClick={() => handleAction('feed')} disabled={!!loadingAction} className="py-3 px-4 bg-green-600 hover:bg-green-700 rounded-md font-semibold transition disabled:bg-gray-500">
                    {loadingAction === 'feed' ? '...' : 'Feed'}
                </button>
                 <button onClick={() => handleAction('play')} disabled={!!loadingAction} className="py-3 px-4 bg-yellow-500 hover:bg-yellow-600 rounded-md font-semibold transition disabled:bg-gray-500">
                    {loadingAction === 'play' ? '...' : 'Play'}
                 </button>
                <button onClick={() => handleAction('sleep')} disabled={!!loadingAction} className="py-3 px-4 bg-green-600 hover:bg-green-700 rounded-md font-semibold transition disabled:bg-gray-500">
                    {loadingAction === 'sleep' ? '...' : 'Sleep'}
                </button>
                 <button onClick={() => handleAction('meds')} disabled={!!loadingAction} className="py-3 px-4 bg-yellow-500 hover:bg-yellow-600 rounded-md font-semibold transition disabled:bg-gray-500">
                    {loadingAction === 'meds' ? '...' : 'Give meds'}
                 </button>
                 {/* Aquí irían los otros botones de acción */}
             </div>
        </div>
    );
};


// -- Página de Detalle de la Mascota --
const PetDetailPage = () => {
    const { petId } = useParams();
    const { user, logout } = useAuth();
    const [pet, setPet] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchPetDetails = async () => {
            try {
                const response = await apiClient.get(`/pet/get/${petId}`);
                console.log("Datos recibidos del backend:", response.data);
                setPet(response.data);
            } catch (err) {
                console.error(`Error al obtener los detalles de la mascota ${petId}:`, err);
                setError('No se pudo encontrar a tu mascota.');
            } finally {
                setLoading(false);
            }
        };

        fetchPetDetails();
    }, [petId]); // Se vuelve a ejecutar si el petId en la URL cambia

    const handlePetUpdate = (updatedPet) => {
        // Actualiza el estado local de la mascota con los datos nuevos del backend
        setPet(updatedPet);
    };

    if (loading) return <div className="text-center text-white text-2xl p-8">Buscando a tu mascota...</div>;
    if (error) return <div className="text-center text-red-500 text-2xl p-8">{error}</div>;
    if (!pet) return <div className="text-center text-white text-2xl p-8">No se encontró la mascota.</div>;

    return (
        <div className="min-h-screen p-4 sm:p-8 text-white">
            <header className="flex justify-between items-center mb-8">
                <Link to="/pets" className="text-indigo-400 hover:underline">{'<'} Volver a la lista</Link>
                <div>
                    <span className="mr-4 text-gray-400 hidden sm:inline">Hola, {user?.username}</span>
                    <button onClick={logout} className="py-2 px-4 bg-red-600 hover:bg-red-700 rounded-md">Cerrar Sesión</button>
                </div>
            </header>

            <div className="max-w-4xl mx-auto">
                <h1 className="text-4xl font-bold text-center mb-8">{pet.name}</h1>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    <PetDisplay pet={pet} />
                    <div className="space-y-8">
                        <PetStats pet={pet} />
                        <PetActions petId={pet.petId} onAction={handlePetUpdate} />
                    </div>
                </div>
            </div>
        </div>
    );
};


// --- 4. COMPONENTE PRINCIPAL Y ENRUTADOR (App.js) ---
function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="bg-gray-900">
          <Routes>
            {/* Rutas públicas */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Rutas protegidas */}
            <Route path="/pets" element={<ProtectedRoute><PetListPage /></ProtectedRoute>} />
            <Route path="/pets/:petId" element={<ProtectedRoute><PetDetailPage /></ProtectedRoute>} />

            {/* Redirección por defecto */}
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
