package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepo.deleteAll();

        adminUser = new User();
        adminUser.setUsername("admin_test");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setRole("ADMIN");
        userRepo.save(adminUser);

        testUser = new User();
        testUser.setUsername("user_test");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole("USER");
        userRepo.save(testUser);

        adminToken = getJwtToken("admin_test", "password");
        userToken = getJwtToken("user_test", "password");
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
    void getAllUsers_whenAuthenticatedAsAdmin_shouldReturnListOfUsers() throws Exception {
        mockMvc.perform(get("/user/getAll")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // We created 2 users in setup
    }

    @Test
    void getAllUsers_whenAuthenticatedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/users/all")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/user/getAll"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void deleteUser_whenAuthenticatedAsAdmin_shouldDeleteUser() throws Exception {
        User userToDelete = new User();
        userToDelete.setUsername("to_delete");
        userToDelete.setPassword(passwordEncoder.encode("password"));
        userToDelete.setRole("USER");
        userToDelete = userRepo.save(userToDelete);
        int userId = userToDelete.getId();

        mockMvc.perform(delete("/user/delete/" + userId) // Assuming this will be the endpoint
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        assertFalse(userRepo.findById(userId).isPresent(), "User should have been deleted from the database");
    }

}