package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.security.JwtUtil;
import cat.itacademy.s05.t02.n01.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

//    @Autowired
//    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserDetailsService userDetailsService;


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());

            authenticationManager.authenticate(authToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User has no roles assigned"));

            String token = jwtUtil.generateToken(request.getUsername(),role);

            log.info("JWT Token generated: {}", token);

            return ResponseEntity.ok(new LoginResponse(token));

    }
}
