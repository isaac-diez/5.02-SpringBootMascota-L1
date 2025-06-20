package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PetServiceImplTest {

    @Mock
    private PetRepo petRepo;

    @Autowired
    private UserRepo userRepo;

    @InjectMocks
    private PetServiceImpl petService;

    @Test
    void createPet_ShouldReturnSavedPet() {

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("isaac");

        PetDto mockDto = new PetDto();
        mockDto.setName("Max");
        mockDto.setType(PetType.TAMAGOTCHI);

        Pet expectedPet = new Pet();
        expectedPet.setPetId(123);
        expectedPet.setName("Max");
        expectedPet.setUser(mockUser);

        when(petRepo.save(any(Pet.class))).thenReturn(expectedPet);

        Pet result = petService.createPet(mockUser, mockDto);

        assertNotNull(result, "The result shouldn't be null");
        assertNotNull(result.getPetId(), "Pet's id shouldn't be null");
        assertEquals("Max", result.getName(), "Name doesn't match");

        verify(petRepo).save(any(Pet.class));
    }

    @Test
    void createPet_WithRealUser_ShouldSavePet() {
        User user = userRepo.findByUsername("isaac")
                .orElseThrow(() -> new UsernameNotFoundException("User isaac not found in DB"));

        PetDto mockDto = new PetDto();
        mockDto.setName("Max");
        mockDto.setType(PetType.TAMAGOTCHI);

        Pet expectedPet = new Pet();
        expectedPet.setPetId(123);
        expectedPet.setName("Max");
        expectedPet.setUser(user);

        when(petRepo.save(any(Pet.class))).thenReturn(expectedPet);

        Pet result = petService.createPet(user, mockDto);

        assertNotNull(result, "The result shouldn't be null");
        assertNotNull(result.getPetId(), "Pet's id shouldn't be null");
        assertEquals("Max", result.getName(), "Name doesn't match");

        verify(petRepo).save(any(Pet.class));
    }

    @Test
    void createAndGetPetById_ShouldReturnCreatedPet() {

        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser_for_pet_id_test");
        testUser.setRole("USER");

        PetDto petDtoToCreate = new PetDto();
        petDtoToCreate.setName("FirulaisConID");
        petDtoToCreate.setType(PetType.ANIMAL);

        ArgumentCaptor<Pet> petArgumentCaptor = ArgumentCaptor.forClass(Pet.class);

        Integer simulatedDbGeneratedId = 88;
        Pet petReturnedBySave = new Pet();

        petReturnedBySave.setName(petDtoToCreate.getName());
        petReturnedBySave.setType(petDtoToCreate.getType());
        petReturnedBySave.setUser(testUser);
        petReturnedBySave.setDob(LocalDateTime.now());
        petReturnedBySave.setDaysOld(0);
        petReturnedBySave.setHealthState(HealthState.OK);
        petReturnedBySave.setHappinessState(HappinessState.SATISFIED);
        petReturnedBySave.setEvolutionState(EvolutionState.BABY);
        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungry(50);initialLevels.setEnergy(80);initialLevels.setHappy(70);
        initialLevels.setHygiene(90);initialLevels.setHealth(100);
        petReturnedBySave.setLevels(initialLevels);

        petReturnedBySave.setPetId(simulatedDbGeneratedId);

        when(petRepo.save(petArgumentCaptor.capture())).thenReturn(petReturnedBySave);

        Pet createdPetResult = petService.createPet(testUser, petDtoToCreate);

        assertNotNull(createdPetResult, "El resultado de createPet no debería ser nulo.");
        assertEquals(simulatedDbGeneratedId, createdPetResult.getPetId(), "El ID de la mascota creada no coincide.");
        assertEquals(petDtoToCreate.getName(), createdPetResult.getName(), "El nombre de la mascota creada no coincide.");

        Pet petPassedToSave = petArgumentCaptor.getValue();
        assertNull(petPassedToSave.getPetId(), "El Pet pasado a save() no debería tener un ID preasignado.");

        when(petRepo.findById(simulatedDbGeneratedId)).thenReturn(Optional.of(petReturnedBySave));

        Optional<Pet> foundPetOptional = petService.getPetById(simulatedDbGeneratedId);

        assertTrue(foundPetOptional.isPresent(), "Se esperaba encontrar la mascota con el ID " + simulatedDbGeneratedId);
        Pet foundPet = foundPetOptional.get();
        assertEquals(simulatedDbGeneratedId, foundPet.getPetId(), "El ID de la mascota encontrada no coincide.");
        assertEquals(petDtoToCreate.getName(), foundPet.getName(), "El nombre de la mascota encontrada no coincide.");
        assertNotNull(foundPet.getUser(), "La mascota encontrada debería tener un usuario asociado.");
        assertEquals(testUser.getUsername(), foundPet.getUser().getUsername(), "El nombre de usuario asociado a la mascota no coincide.");

        verify(petRepo).save(any(Pet.class));
        verify(petRepo).findById(simulatedDbGeneratedId);
    }
}