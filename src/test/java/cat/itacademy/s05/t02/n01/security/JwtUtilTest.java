package cat.itacademy.s05.t02.n01.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        // 2. Genera token
        String token = jwtUtil.generateToken(userDetails.getUsername(), Collections.singleton(new SimpleGrantedAuthority("USER")));
        System.out.println(token);

        // 3. Verificaciones
        assertNotNull(token);
        assertTrue(token.length() > 30); // Verifica longitud mínima
        assertTrue(jwtUtil.validateToken(token));
    }


}