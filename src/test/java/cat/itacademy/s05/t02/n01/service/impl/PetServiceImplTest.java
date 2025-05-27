package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;
import cat.itacademy.s05.t02.n01.model.PetType;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.PetService;
import cat.itacademy.s05.t02.n01.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PetServiceImplTest {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @MockBean
    private PetRepo petRepo;

    @Test
    void createPet_ShouldReturnSavedPet() {
        // Preparación
        User user = (User) userService.loadUserById(8);

        PetDTO dto = new PetDTO();
        dto.setPetName("Max");
        dto.setPetType(PetType.TAMAGOTCHI);

        // Ejecución
        Pet result = petService.createPet(user, dto);

        // Verificación
        assertNotNull(result.getId());
        assertEquals("Max", result.getName());
    }
}