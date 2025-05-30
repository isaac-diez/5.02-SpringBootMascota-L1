package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.security.JwtRequestFilter;
import cat.itacademy.s05.t02.n01.security.JwtUtil;
import cat.itacademy.s05.t02.n01.service.UserService;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest_NEW {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private AuthController authController;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
        // Inyecta jwtUtil manualmente porque no usamos Spring Context
        authController.jwtUtil = jwtUtil;
    }

    @Test
    void login_ShouldReturnJwtToken() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testuser", "testpass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Simula autenticacion sin error
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).
                thenReturn(null);
        // Devuelve userDetails esperado
        when(userService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // Act
        var responseEntity = authController.login(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().token().length() > 20);

        System.out.println("Generated token: " + responseEntity.getBody().token());
    }

    @Test
    void jwtRequestFilter_ShouldAllowValidToken() throws Exception {
        // Arrange
        String username = "testuser";
        String role = "ROLE_USER";

        // Genera token válido con JwtUtil
        String token = jwtUtil.generateToken(username, role);

        // Prepara mock request con header Authorization Bearer token
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        ServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(jwtUtil, userService);

        // Mock para cargar usuario dentro del filtro
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "ignored-password",
                Collections.singletonList(new SimpleGrantedAuthority(role)));
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act: pasa por el filtro
        jwtRequestFilter.doFilter(request, response, filterChain);

        // Assert: verifica que el filtro continúe la cadena
        verify(filterChain, times(1)).doFilter(request, response);
    }
}

