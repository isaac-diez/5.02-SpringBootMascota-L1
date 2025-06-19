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
        // ---- ARRANGE (Preparación) ----

        // 1. Prepara el User
        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser_for_pet_id_test");
        testUser.setRole("USER");

        // 2. Prepara el PetDTO para la creación
        PetDto petDtoToCreate = new PetDto();
        petDtoToCreate.setName("FirulaisConID");
        petDtoToCreate.setType(PetType.ANIMAL);

        // 3. Define el objeto Pet tal como se pasaría al método save (sin ID asignado por nosotros)
        //    y también el objeto Pet como se esperaría que fuera DEVUELTO por save() (con un ID).

        // ArgumentCaptor para capturar la mascota que se pasa a petRepo.save()
        ArgumentCaptor<Pet> petArgumentCaptor = ArgumentCaptor.forClass(Pet.class);

        // Este es el objeto Pet que simularemos que es devuelto por petRepo.save()
        // YA CON UN ID ASIGNADO (simulando la acción de la BD).
        Integer simulatedDbGeneratedId = 88; // Un ID de ejemplo que la BD "generaría"
        Pet petReturnedBySave = new Pet();
        // Copiamos las propiedades que createPet establece antes de llamar a save,
        // y luego le asignamos el ID simulado.
        petReturnedBySave.setName(petDtoToCreate.getName());
        petReturnedBySave.setType(petDtoToCreate.getType());
        petReturnedBySave.setUser(testUser);
        petReturnedBySave.setDob(LocalDateTime.now()); // Simular lo que hace createPet
        petReturnedBySave.setDaysOld(0);
        petReturnedBySave.setHealthState(HealthState.OK);
        petReturnedBySave.setHappinessState(HappinessState.SATISFIED);
        petReturnedBySave.setEvolutionState(EvolutionState.BABY);
        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungry(50);initialLevels.setEnergy(80);initialLevels.setHappy(70);
        initialLevels.setHygiene(90);initialLevels.setHealth(100);
        petReturnedBySave.setLevels(initialLevels);

        // IMPORTANTE: Asignamos el ID al objeto que el mock de save() devolverá.
        petReturnedBySave.setPetId(simulatedDbGeneratedId);

        // Configura el mock de petRepo.save()
        // Cuando se llame, capturará el argumento y devolverá nuestra instancia con ID.
        when(petRepo.save(petArgumentCaptor.capture())).thenReturn(petReturnedBySave);

        // ---- ACT (Ejecución de la creación) ----
        Pet createdPetResult = petService.createPet(testUser, petDtoToCreate);

        // ---- ASSERT (Verificación de la creación) ----
        assertNotNull(createdPetResult, "El resultado de createPet no debería ser nulo.");
        // El ID debe ser el que simulamos que la BD generó y que nuestro mock de save devolvió.
        assertEquals(simulatedDbGeneratedId, createdPetResult.getPetId(), "El ID de la mascota creada no coincide.");
        assertEquals(petDtoToCreate.getName(), createdPetResult.getName(), "El nombre de la mascota creada no coincide.");

        // Opcional: Verificar que el objeto pasado a save no tenía ID (o era 0/null)
        Pet petPassedToSave = petArgumentCaptor.getValue();
        assertNull(petPassedToSave.getPetId(), "El Pet pasado a save() no debería tener un ID preasignado.");

        // ---- ARRANGE (Preparación para getPetById) ----
        // Configura el mock de petRepo.findById() para que devuelva la mascota
        // que "creamos" (la que tiene el ID simulado).
        when(petRepo.findById(simulatedDbGeneratedId)).thenReturn(Optional.of(petReturnedBySave));

        // ---- ACT (Ejecución de getPetById) ----
        Optional<Pet> foundPetOptional = petService.getPetById(simulatedDbGeneratedId);

        // ---- ASSERT (Verificación de getPetById) ----
        assertTrue(foundPetOptional.isPresent(), "Se esperaba encontrar la mascota con el ID " + simulatedDbGeneratedId);
        Pet foundPet = foundPetOptional.get();
        assertEquals(simulatedDbGeneratedId, foundPet.getPetId(), "El ID de la mascota encontrada no coincide.");
        assertEquals(petDtoToCreate.getName(), foundPet.getName(), "El nombre de la mascota encontrada no coincide.");
        assertNotNull(foundPet.getUser(), "La mascota encontrada debería tener un usuario asociado.");
        assertEquals(testUser.getUsername(), foundPet.getUser().getUsername(), "El nombre de usuario asociado a la mascota no coincide.");

        // Verifica que los métodos mockeados fueron llamados como se esperaba
        verify(petRepo).save(any(Pet.class)); // O ser más específico con petPassedToSave
        verify(petRepo).findById(simulatedDbGeneratedId);
    }
}