# Digital Pet API (Backend)

This is the backend for the Digital Pet application, a RESTful API built with Spring Boot. It handles user authentication, pet data management, and the core game logic for the virtual pets.

## Tech Stack

The backend is built with a modern Java ecosystem:
- **Framework**: Spring Boot 3
- **Language**: Java 17
- **Security**: Spring Security 6, with JWT (JSON Web Tokens) for stateless authentication.
- **Database**: Spring Data JPA with Hibernate connecting to a MySQL database.
- **API Documentation**: Swagger/OpenAPI for documenting the REST endpoints.
- **Build Tool**: Apache Maven
- **Utilities**: Lombok to reduce boilerplate code.

## Authentication Flow

The application uses a stateless JWT-based authentication system:
1.  A user sends their `username` and `password` to the `POST /auth/login` endpoint.
2.  The `AuthController` uses Spring Security's `AuthenticationManager` to validate the credentials.
3.  Upon successful authentication, the `JwtUtil` class generates a JWT containing the username and their role (e.g., `ROLE_USER` or `ROLE_ADMIN`).
4.  This token is returned to the client. The client must then include this token in the `Authorization` header for all subsequent requests to protected endpoints (e.g., `Authorization: Bearer <token>`).
5.  The `JwtRequestFilter` intercepts every incoming request. It validates the token and sets the user's authentication context, allowing Spring Security to handle authorization based on the user's role.
6.  CORS preflight `OPTIONS` requests are automatically permitted to ensure smooth communication with the frontend application.

## Project Structure

```
VirtualPet/
├── controller/         # REST controllers
├── services/           # Business logic
├── model/              # Entities and DTOs
├── repositories/       # MySQL access
├── utils/              # Another files
├── config/             # JWT and security configuration
├── dto/                # Data transfer objects    
├── resources/
│   └── application.properties
└── VirtualPetApplication.java
```

The codebase is organized into logical packages:

-   `controller`: Defines the REST API endpoints. It routes incoming HTTP requests to the appropriate services.
    -   `AuthController`: Handles user login and registration.
    -   `PetController`: Manages all pet-related actions like creation, fetching, and interactions (play, feed, etc.).
    -   `AdminController`: Provides endpoints exclusively for users with the 'ADMIN' role.
-   `model`: Contains the JPA entity classes (`User`, `Pet`, `Event`) and Enums (`HealthState`, `EvolutionState`) that map to database tables.
-   `service`: Holds the core business logic.
    -   `PetServiceImpl`: Contains the complex game logic for creating pets, updating their needs over time via a `@Scheduled` task, and handling user actions.
    -   `UserService`: Implements Spring Security's `UserDetailsService` to load user data for authentication and handles user creation.
-   `repo`: Spring Data JPA repositories (`UserRepo`, `PetRepo`) that provide an abstraction layer for database operations.
-   `security`: Contains all classes related to Spring Security and JWT configuration.
-   `dto`: Data Transfer Objects (`PetDto`, `UserDto`) and Mappers used to shape the data exposed by the API.
-   `exception`: Custom exception classes for handling specific application errors, like `PetNotFoundException`.

## Key API Endpoints

### Authentication (`/auth`)
* `POST /login`: Authenticates a user and returns a JWT.
* `POST /register`: Creates a new user with the `USER` role.

### Pet Management (`/pet`)
* `POST /new`: Creates a new pet for the authenticated user.
* `GET /my-pets`: Returns a list of all pets owned by the authenticated user.
* `GET /get/{id}`: Returns the detailed information for a specific pet.
* `POST /{id}/{action}`: Applies an action (e.g., `play`, `feed`, `clean`) to a pet.
* `DELETE /delete/{id}`: Deletes a specific pet.

### Administration (`/admin`)
* `GET /users/all`: (Requires `ADMIN` role) Returns a list of all users in the system.
* `GET /pets/all`: (Requires `ADMIN` role) Returns a list of all pets in the system.

## Setup and Installation

1.  Clone the repository.
2.  Ensure you have a MySQL database created (e.g., `tamagotchi`).
3.  Update the `spring.datasource.url`, `username`, and `password` in `src/main/resources/application.properties` with your database credentials.
4.  Run the application using your IDE or with the Maven command:
    ```sh
    mvn spring-boot:run
    ```
5.  The API will be available at `http://localhost:8080`.


## 🔗 Key Endpoints

Swagger UI is available at:

📚 ```http://localhost:8080/swagger-ui/index.html```

# Digital Pet UI (Frontend)

This is the fronten for the Digital Pet application, a dynamic and responsive Single-Page Application (SPA) built with React. It provides a user-friendly interface to interact with the backend API, manage virtual pets, and access administrative features.

## Tech Stack & Key Libraries

-   **Framework**: React
-   **Routing**: `react-router-dom` for handling all client-side navigation and protected routes.
-   **HTTP Client**: `axios` for making clear and concise API calls to the Spring Boot backend.
-   **Styling**: Tailwind CSS for a modern, utility-first approach to responsive design.
-   **State Management**: React Context API (`AuthContext`) for managing global application state, such as user authentication and session tokens.

## Key Features

-   **Full-Stack Authentication**: Secure login and registration forms that communicate with the backend to obtain a JWT, which is then stored and used for all protected API calls.

-   **Role-Based Access Control (RBAC)**: The application features a robust routing system that directs users to different experiences based on their role:
    -   **Users** are sent to their pet list (`/pets`).
    -   **Admins** are sent to a comprehensive admin dashboard (`/admin`).

-   **Dynamic Pet Interaction**:
    -   **Pet List Page**: Users can see a gallery of all their pets, with key stats and state-aware images.
    -   **Pet Detail Page**: A dedicated page for each pet showing detailed stats and providing action buttons (`Feed`, `Play`, `Clean`, `Give Medicine`).
    -   **State-Aware UI**: The interface provides clear visual feedback. Action buttons are disabled and pet cards are grayed out if a pet's `evolutionState` is `DEAD`.

-   **Comprehensive Admin Dashboard**:
    -   A private, admin-only view that lists all users and all pets in the entire system.
    -   Functionality to manage the application, including deleting users and pets directly from the dashboard.
    -   Calculated views, such as displaying the number of pets each user owns.

-   **Modern UI/UX**:
    -   A consistent and reusable component-based architecture (`PetCard`, `PetImage`, `PetActions`, `AuthLayout`).
    * A visually appealing login/register screen with a full-screen background image and a readable, blurred form container.
    -   Sticky navigation bars for easy access to primary actions like creating a new pet or logging out.

## Project Structure

The frontend codebase is organized for clarity and scalability:

-   `src/pages`: Contains the top-level components for each distinct page or view in the application (e.g., `LoginPage.jsx`, `AdminPage.jsx`, `PetListPage.jsx`).
-   `src/components`: Holds reusable UI components, organized into subdirectories by feature.
    -   `auth/`: Components related to authentication, like `AuthForm.jsx`.
    -   `pets/`: Components for displaying pet information, like `PetCard.jsx`, `PetImage.jsx`, and `PetActions.jsx`.
    -   `layout/`: Components that define the overall page structure, like `AuthLayout.jsx`.
    -   `common/`: Shared components like `ProtectedRoute.jsx`.
-   `src/context`: Home to React Context providers for managing global state across the application, most importantly `AuthContext.jsx`.
-   `src/api`: Contains the configured `axios` instance (`apiClient.js`), which centralizes the base URL and handles API requests.

## Screenshots

Here are some representative views of the application:

**Login Screen with Blurred Background**
(![login_screenshot.jpg](../login_screenshot.jpg) "Login page with a full-screen background image and a centered, readable form.")

**User's Pet List Page**
![petlist_screenshot.jpeg](../petlist_screenshot.jpeg) "A grid view showing a user's collection of pets, with state-aware images and basic info.")

**Pet Detail Page with Actions**
![petdetail_screenshot.jpeg](../petdetail_screenshot.jpeg) "A detailed view of a single pet, showing its image, stats, and a list of action buttons.")

**Admin Dashboard**
![admin_screenshot.png](../admin_screenshot.png) "The admin-only view showing lists of all users and all pets in the system, with management options.")


## Setup and Installation

1.  Navigate to the frontend project directory.
2.  Install the necessary dependencies:
    ```sh
    npm install
    ```
3.  Ensure the backend API server is running (usually on `localhost:8080`). The frontend is configured in `src/api/apiClient.js` to connect to this address.
4.  Start the frontend development server:
    ```sh
    npm run dev
    ```
5.  The application will be available at `http://localhost:5173` (or another port if specified).