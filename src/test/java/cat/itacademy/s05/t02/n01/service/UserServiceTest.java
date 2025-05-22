package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void loadUserByUsername() {

    }

    @Transactional
    @Test
    void createUser() {

        List<String> roles = List.of("ROLE_USER");
        User user = userService.createUser("test", "isaac", roles);

        Assertions.assertEquals("test", user.getUsername());
        Assertions.assertEquals(1, user.getRoles().size());
        Assertions.assertEquals("ROLE_USER", user.getRoles().get(0).getRoleType());

    }
}