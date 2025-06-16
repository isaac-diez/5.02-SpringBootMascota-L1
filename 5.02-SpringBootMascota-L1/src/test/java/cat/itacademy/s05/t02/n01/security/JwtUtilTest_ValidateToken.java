package cat.itacademy.s05.t02.n01.security;

import cat.itacademy.s05.t02.n01.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({JwtRequestFilter.class, JwtUtil.class})
class JwtUtilTest_ValidateToken {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            return username -> {
                if (username.equals("testuser")) {
                    return new User("testuser", new BCryptPasswordEncoder().encode("password"),
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                }
                throw new UsernameNotFoundException("User not found");
            };
        }
    }

    @TestConfiguration
    static class DummyBeans {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    void validToken_ShouldAuthenticate() throws Exception {
        // 1. Prepara token válido
        String token = jwtUtil.generateToken("testuser", "ROLE_USER");

        // 3. Ejecuta petición con token
        mockMvc.perform(get("/user/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}