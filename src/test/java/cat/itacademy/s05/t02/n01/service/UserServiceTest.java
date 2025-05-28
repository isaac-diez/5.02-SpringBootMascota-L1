package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.Role;
import cat.itacademy.s05.t02.n01.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

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
        Role role = new Role();
        role.setRoleType("ROLE_ADMIN");

        User user = new User();
        user.setUsername("TestLoadUser");
        user.setPassword("1234");
        user.setRoles(Set.of(role));

        when(userRepo.findByUsername("TestLoadUser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("TestLoadUser");

        assertEquals("TestLoadUser", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));


    }

    @Test
    void testLoadUserByUsername_whenUserNotFound_throwsException() {

        when(userRepo.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Test
    void testCreateNewUser() {

        Role role = new Role();
        role.setRoleType("ROLE_USER");
        List<String> roles = List.of("ROLE_USER");

        User user = new User();
        user.setUsername("TestCreateUser");
        user.setPassword("1234");
        user.setRoles(Set.of(role));

        userService.createUser(user.getUsername(),user.getPassword(), roles);

        when(userRepo.findByUsername("TestCreateUser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("TestCreateUser");

        assertEquals("TestCreateUser", userDetails.getUsername());
        assertEquals("1234", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}