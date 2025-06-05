package cat.itacademy.s05.t02.n01.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

// Tus importaciones de modelo, repo, DTO, etc.
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.model.PetType;
// Importa ObjectMapper para convertir objetos a JSON
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is; // Para jsonPath


@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Muy recomendado para que cada test tenga una BD limpia
class PetControllerIntegrationTest { // Renombrado para claridad

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PetRepo petRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Limpiar repositorios (opcional si @Transactional funciona bien, pero puede dar más control)
        petRepo.deleteAll();
        userRepo.deleteAll();

        // 1. Crear y guardar un usuario de prueba
        testUser = new User();
        testUser.setUsername("testuser_integration");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole("USER"); // Rol sin prefijo ROLE_
        testUser = userRepo.save(testUser);

        // 2. Obtener un token JWT para este usuario
        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                testUser.getUsername(), "password123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        // Extraer el token de la respuesta JSON (asumiendo que LoginResponse tiene un campo "token")
        // Esto podría necesitar una clase DTO o parseo JSON más robusto si la estructura es compleja
        jwtToken = objectMapper.readTree(loginResponse).get("token").asText();
    }

    @Test
    void createPetAndThenGetPetById_WithAuthentication_ShouldSucceed() throws Exception {
        // ---- PARTE 1: Crear la Mascota (a través del endpoint de PetController) ----

        // PetDTO para la creación
        PetDto petDtoToCreate = new PetDto();
        petDtoToCreate.setPetName("IntegrationRex");
        petDtoToCreate.setPetType(PetType.TAMAGOTCHI);

        // Petición para crear la mascota, usando el token JWT
        // Asumo que tu endpoint de crear Pet es POST /pets y espera un userId en el header
        // o que el servicio lo deduce del usuario autenticado.
        // Si PetController.createPet espera userId en header, ajústalo.
        // Si deduce el User del SecurityContext, no necesitas pasarlo.
        // Por ahora, asumiré que el userId se pasa en el header (si es así)
        // o que tu PetController está diseñado para obtener el User del contexto de seguridad.
        // Si tu endpoint POST /pets (o /pet/new) obtiene el User del Principal:
        MvcResult createPetResult = mockMvc.perform(post("/pet/new") // O la ruta correcta de tu PetController para crear
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDtoToCreate)))
                .andExpect(status().isCreated()) // O isOk() dependiendo de tu implementación
                .andExpect(jsonPath("$.petName", is(petDtoToCreate.getPetName())))
                .andReturn();

        String createPetResponse = createPetResult.getResponse().getContentAsString();
        // Asumiendo que la respuesta de crear Pet contiene el ID de la mascota creada.
        // (Si tu PetDTO devuelto tiene un campo 'id' o similar, o si devuelves Pet)
        // Necesitas extraer el ID de la mascota creada de 'createPetResponse'.
        // Por ejemplo, si devuelves el Pet completo con su ID:
        PetDto createdPet = objectMapper.readValue(createPetResponse, PetDto.class);
        Integer createdPetId = createdPet.getId(); // Asegúrate de que Pet tiene getPetId()

        assertNotNull(createdPetId, "El ID de la mascota creada no debería ser nulo");

        // ---- PARTE 2: Obtener la Mascota por ID (a través del endpoint de PetController) ----

        // Petición para obtener la mascota por su ID, usando el token JWT

        mockMvc.perform(get("/pet/get/" + createdPetId) // Ruta correcta de tu PetController
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.petId", is(createdPetId)))
                .andExpect(jsonPath("$.name", is(petDtoToCreate.getPetName())))
                .andExpect(jsonPath("$.type", is(petDtoToCreate.getPetType().toString()))) // toString() para enums en JSON
                .andExpect(jsonPath("$.userId", is(testUser.getId_user()))); // Verifica el usuario asociado
        // Ajusta los jsonPath según la estructura exacta de tu respuesta Pet serializada
    }
}