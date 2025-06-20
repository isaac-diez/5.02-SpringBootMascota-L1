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