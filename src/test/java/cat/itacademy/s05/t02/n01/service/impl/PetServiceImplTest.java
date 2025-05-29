package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;
import cat.itacademy.s05.t02.n01.model.PetType;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.UserDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PetServiceImplTest {

    @Mock
    private PetRepo petRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private PetServiceImpl petService;


    @Test
    void createPet_ShouldReturnSavedPet() {

        User mockUser = new User();
        mockUser.setId_user(1);
        mockUser.setUsername("Isaac");

        PetDTO mockDto = new PetDTO();
        mockDto.setPetName("Max");
        mockDto.setPetType(PetType.TAMAGOTCHI);

        Pet expectedPet = new Pet();
        expectedPet.setPet_id("pet123");
        expectedPet.setName("Max");
        expectedPet.setUser(mockUser);

        when(petRepo.save(any(Pet.class))).thenReturn(expectedPet);

        Pet result = petService.createPet(mockUser, mockDto);

        assertNotNull(result, "The result shouldn't be null");
        assertNotNull(result.getPet_id(), "Pet's id shouldn't be null");
        assertEquals("Max", result.getName(), "Name doesn't match");

        verify(petRepo).save(any(Pet.class));
    }

    @Test
    void createPet_WithRealUser_ShouldSavePet() {
        User user = userRepo.findByUsername("Isaac")
                .orElseThrow(() -> new UsernameNotFoundException("User Isaac not found in DB"));

        PetDTO mockDto = new PetDTO();
        mockDto.setPetName("Max");
        mockDto.setPetType(PetType.TAMAGOTCHI);

        Pet expectedPet = new Pet();
        expectedPet.setPet_id("pet123");
        expectedPet.setName("Max");
        expectedPet.setUser(user);

        when(petRepo.save(any(Pet.class))).thenReturn(expectedPet);

        Pet result = petService.createPet(user, mockDto);

        assertNotNull(result, "The result shouldn't be null");
        assertNotNull(result.getPet_id(), "Pet's id shouldn't be null");
        assertEquals("Max", result.getName(), "Name doesn't match");

        verify(petRepo).save(any(Pet.class));
    }
}