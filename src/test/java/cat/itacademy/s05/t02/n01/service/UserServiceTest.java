package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    void testLoadUserByUsername_whenUserExists_returnsUserDetails() {

        User user = new User();
        user.setUsername("TestLoadUser");
        user.setPassword("1234");
        user.setRole("ADMIN");

        when(userRepo.findByUsername("TestLoadUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("TestLoadUser");

        assertEquals("TestLoadUser", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));


    }

    @Test
    void testLoadUserByUsername_whenUserNotFound_throwsException() {

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Transactional
    @Test
    void testCreateNewUser() {

        String userName = "TestCreateUser";
        String rawPassword = "1234";

        User expectedUserFromDB = new User();
        expectedUserFromDB.setUsername(userName);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        expectedUserFromDB.setPassword(encodedPassword);
        expectedUserFromDB.setRole("USER");

        when(userRepo.save(any(User.class))).thenReturn(expectedUserFromDB);

        User createdUser = userService.createUser(userName, rawPassword);

        assertNotNull(createdUser);
        assertTrue(encoder.matches(rawPassword,createdUser.getPassword()));
        assertEquals("USER", createdUser.getRole());

        when(userRepo.findByUsername(userName)).thenReturn(Optional.of(expectedUserFromDB));

        UserDetails userDetails = userService.loadUserByUsername(userName);

        assertEquals(userName, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")),
                "La autoridad ROLE_USER no fue encontrada. Autoridades: " + userDetails.getAuthorities());    }
}