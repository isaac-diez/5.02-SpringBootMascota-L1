package cat.itacademy.s05.t02.n01.security;

import cat.itacademy.s05.t02.n01.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({JwtRequestFilter.class, JwtUtil.class})
class JwtUtilTest_ValidateToken {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Test
    void validToken_ShouldAuthenticate() throws Exception {
        // 1. Prepara token válido
        UserDetails user = new User("testuser", "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        String token = new JwtUtil().generateToken(user.getUsername(),Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        // 2. Configura mock
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);

        // 3. Ejecuta petición con token
        mockMvc.perform(get("/user/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}