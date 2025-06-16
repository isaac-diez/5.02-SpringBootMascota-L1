package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
@Transactional
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("testpass"));
        user.setRole("USER");
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
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        log.info("Login Response: {}", responseBody);

    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        String loginRequestJson = "{\"username\":\"testuser\",\"password\":\"wrongpassword\"}";

        log.info("Attempting login with invalid payload: {}", loginRequestJson);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isUnauthorized());
    }
}