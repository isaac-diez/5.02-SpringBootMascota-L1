package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.model.Role;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.security.JwtUtil;
import cat.itacademy.s05.t02.n01.service.CustomUserDetailsService;
import cat.itacademy.s05.t02.n01.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    private final JwtUtil jwtUtil;
//
//    @Autowired
//    private UserService userService;
//
//    public AuthController(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
//        User user = (User) userService.loadUserByUsername(username);
//        if (user != null && user.getPassword().equals(password)) {
//            String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
//            return ResponseEntity.ok(Map.of("token", token));
//        }
//        return ResponseEntity.status(401).body("Invalid credentials");
//    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());

            authenticationManager.authenticate(authToken);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
            Set<GrantedAuthority> roles = userDetails.getAuthorities().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                    .collect(Collectors.toSet());

            String token = jwtUtil.generateToken(request.getUsername(),roles);

            log.info("JWT Token generated: {}", token);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
