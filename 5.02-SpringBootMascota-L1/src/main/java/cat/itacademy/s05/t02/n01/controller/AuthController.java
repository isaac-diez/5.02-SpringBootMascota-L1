package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.exception.UsernameAlreadyInDataBaseException;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.security.JwtUtil;
import cat.itacademy.s05.t02.n01.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());
            log.info("Attempting to authenticate user: {} with token: {}", request.getUsername(), authToken);

            authenticationManager.authenticate(authToken);

            log.info("Authentication successful for user: {}", request.getUsername());

            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User has no roles assigned. User: " + request.getUsername()));
            String token = jwtUtil.generateToken(request.getUsername(), role);
            log.info("JWT Token generated for user: {}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (AuthenticationException e) {

            log.warn("Authentication failed for user {}: {} - {}",
                    request.getUsername(), e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {} - {}",
                    request.getUsername(), e.getClass().getSimpleName(), e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Error interno durante el login: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
        try {
            User newUser = userService.createUser(request.getUsername(), request.getPassword());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User '" + newUser.getUsername() + "' successfully registered");
        } catch (UsernameAlreadyInDataBaseException e) {
            log.warn("Register attempt failed since user already exists: {}", request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpectet error during user registration: {}", request.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error during registration");
        }
    }

}