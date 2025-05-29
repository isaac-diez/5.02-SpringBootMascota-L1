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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        User user = new User();
        user.setUsername("TestCreateUser");
        user.setPassword("1234");
        user.setRole("USER");

        when(userRepo.findByUsername("TestCreateUser")).thenReturn(Optional.of(user));

        userService.createUser(user.getUsername(),user.getPassword(),user.getRole());

        UserDetails userDetails = userService.loadUserByUsername("TestCreateUser");

        assertEquals("TestCreateUser", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}