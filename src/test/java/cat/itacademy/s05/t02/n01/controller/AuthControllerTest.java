package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @BeforeEach
    void setUp() {
        // Limpiar el repositorio antes de cada test si es necesario,
        // aunque @Transactional debería encargarse del rollback.
        // userRepo.deleteAll(); // Opcional, dependiendo de tu estrategia de test

        // Configura el usuario una vez antes del test
        User user = new User();
        user.setUsername("testuser");
        // Asegúrate que la contraseña que guardas es la que usarás en el login
        user.setPassword(passwordEncoder.encode("testpass"));
        user.setRole("ROLE_USER"); // Asegúrate que este rol es el que espera tu lógica
        userRepo.save(user);
        log.info("User saved for test: {}", user.getUsername());
    }

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() throws Exception {
        String loginRequestJson = "{\"username\":\"testuser\",\"password\":\"testpass\"}";

        log.info("Attempting login with payload: {}", loginRequestJson);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()) // Verifica que el token exista
                .andExpect(jsonPath("$.token").isString()) // Verifica que el token sea un string
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        log.info("Login Response: {}", responseBody);
        // Puedes añadir más aserciones sobre el token si es necesario
        // por ejemplo, si puedes decodificarlo (aunque eso sería probar JwtUtil más que AuthController)
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        String loginRequestJson = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";

        log.info("Attempting login with invalid payload: {}", loginRequestJson);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                // Esperarías un 401 Unauthorized o similar, no RuntimeException directamente.
                // Esto depende de cómo manejes AuthenticationException.
                // Si tu controller lo convierte en RuntimeException, el test fallará
                // porque Spring Boot por defecto mapea RuntimeException a 500 Internal Server Error.
                // Deberías tener un @ControllerAdvice para manejar AuthenticationException
                // y devolver un ResponseEntity con estado 401.
                .andExpect(status().isUnauthorized()); // O el estado que devuelvas para credenciales inválidas
    }
}