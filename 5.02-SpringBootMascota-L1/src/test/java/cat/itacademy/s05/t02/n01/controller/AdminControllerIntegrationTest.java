package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PetRepo petRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        petRepo.deleteAll();
        userRepo.deleteAll();

        User adminUser = new User();
        adminUser.setUsername("admin_test");
        adminUser.setPassword(passwordEncoder.encode("adminpass"));
        adminUser.setRole("ADMIN");
        userRepo.save(adminUser);

        User regularUser = new User();
        regularUser.setUsername("user_test");
        regularUser.setPassword(passwordEncoder.encode("userpass"));
        regularUser.setRole("USER");
        regularUser = userRepo.save(regularUser);

        Pet pet = new Pet();
        pet.setName("TestPetForAdminTest");
        pet.setType(PetType.POKEMON);
        pet.setUser(regularUser);
        pet.setDob(LocalDateTime.now());
        pet.setDaysOld(0);
        pet.setHealthState(HealthState.OK); // Set default health state
        pet.setHappinessState(HappinessState.SATISFIED); // Set default happiness state
        pet.setEvolutionState(EvolutionState.BABY); // Set default evolution state

        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungry(50);
        initialLevels.setEnergy(80);
        initialLevels.setHappy(70);
        initialLevels.setHygiene(90);
        initialLevels.setHealth(100);
        pet.setLevels(initialLevels);

        petRepo.save(pet);

        adminToken = getJwtToken("admin_test", "adminpass");
        userToken = getJwtToken("user_test", "userpass");
    }

    private String getJwtToken(String username, String password) throws Exception {
        String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
        return new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @DisplayName("GET /admin/pets/all - As Admin - Should return all pets")
    void getAllPetsForAdmin_whenAuthenticatedAsAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/admin/pets/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // We created 1 pet in setup
                .andExpect(jsonPath("$[0].name", is("TestPetForAdminTest")));
    }

    @Test
    @DisplayName("GET /admin/users/all - As Admin - Should return all users")
    void getAllUsers_whenAuthenticatedAsAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/admin/users/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /admin/users/all - As User - Should return 403 Forbidden")
    void getAllUsers_whenAuthenticatedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/users/all")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}