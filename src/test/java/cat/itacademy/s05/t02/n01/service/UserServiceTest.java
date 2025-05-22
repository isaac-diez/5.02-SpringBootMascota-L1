package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void loadUserByUsername() {
    }

    @Test
    void createUser() {

        List<String> roles = List.of("ROLE_ADMIN");
        User user = userService.createUser("isaac", "isaac", roles);

        Assertions.assertEquals("isaac", user.getUsername());
        Assertions.assertEquals(1, user.getRoles().size());
        Assertions.assertEquals("ROLE_USER", user.getRoles().get(0).getRoleType());

    }
}