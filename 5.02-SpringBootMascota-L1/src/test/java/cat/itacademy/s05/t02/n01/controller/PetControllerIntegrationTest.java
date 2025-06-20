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

import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.model.PetType;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PetControllerIntegrationTest {

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
        petRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser_integration");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole("USER"); // Rol sin prefijo ROLE_
        testUser = userRepo.save(testUser);

        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                testUser.getUsername(), "password123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(loginResponse).get("token").asText();
    }

    @Test
    void createPetAndThenGetPetById_WithAuthentication_ShouldSucceed() throws Exception {

        PetDto petDtoToCreate = new PetDto();
        petDtoToCreate.setName("IntegrationRex");
        petDtoToCreate.setType(PetType.TAMAGOTCHI);


        MvcResult createPetResult = mockMvc.perform(post("/pet/new")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDtoToCreate)))
                .andExpect(status().isCreated()) // O isOk() dependiendo de tu implementación
                .andExpect(jsonPath("$.petName", is(petDtoToCreate.getName())))
                .andReturn();

        String createPetResponse = createPetResult.getResponse().getContentAsString();

        PetDto createdPet = objectMapper.readValue(createPetResponse, PetDto.class);
        Integer createdPetId = createdPet.getPetId(); // Asegúrate de que Pet tiene getPetId()

        assertNotNull(createdPetId, "El ID de la mascota creada no debería ser nulo");

        mockMvc.perform(get("/pet/get/" + createdPetId) // Ruta correcta de tu PetController
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.petId", is(createdPetId)))
                .andExpect(jsonPath("$.name", is(petDtoToCreate.getName())))
                .andExpect(jsonPath("$.type", is(petDtoToCreate.getType().toString()))) // toString() para enums en JSON
                .andExpect(jsonPath("$.userId", is(testUser.getId()))); // Verifica el usuario asociado
    }
}