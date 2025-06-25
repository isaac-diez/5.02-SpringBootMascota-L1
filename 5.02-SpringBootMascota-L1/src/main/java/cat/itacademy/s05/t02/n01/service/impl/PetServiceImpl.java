package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.dto.PetMapper;
import cat.itacademy.s05.t02.n01.exception.EmptyPetListException;
import cat.itacademy.s05.t02.n01.exception.PetNotFoundException;
import cat.itacademy.s05.t02.n01.exception.UnauthorizedUserException;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.service.PetService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);


    @Autowired
    private PetRepo petRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private PetMapper petMapper;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void updateAllPetsPassiveStates() {
        log.info("Ejecutando tarea programada: Actualizando estados pasivos de las mascotas...");

        List<Pet> activePets = petRepo.findAllByEvolutionStateNot(EvolutionState.DEAD);

        if (activePets.isEmpty()) {
            log.info("No hay mascotas activas para actualizar.");
            return;
        }

        for (Pet pet : activePets) {
            try {
                log.trace("Actualizando mascota con ID: {}", pet.getPetId());

                pet.updateNeeds();
                pet.updateDerivedStates();

                Pet updatedPet = petRepo.save(pet);

                // --- NEW: Send update to the specific user via WebSocket ---
                if (updatedPet.getUser() != null) {
                    // Convert the updated pet to a DTO to send to the frontend
                    var petDto = petMapper.toDetailDto(updatedPet);
                    String username = updatedPet.getUser().getUsername();

                    log.info("Pushing update for pet {} to user {}", petDto.getPetId(), username);
                    // The destination is specific to the user, e.g., /user/isaac/queue/pet-updates
                    messagingTemplate.convertAndSendToUser(
                            username,
                            "/queue/pet-updates",
                            petDto
                    );
                }

            } catch (Exception e) {
                log.error("Error al actualizar la mascota con ID {}: {}", pet.getPetId(), e.getMessage());
            }
        }

        log.info("Tarea de actualización de mascotas completada. Mascotas procesadas: {}", activePets.size());
    }


    public Pet createPet(User user, PetDto petDto) {

        if (user == null || petDto == null) {
            throw new IllegalArgumentException("User and PetDTO can't be null");
        }

        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setType(petDto.getType());
        pet.setUser(user);
        pet.setDob(LocalDateTime.now());
        pet.setDaysOld(0);
        pet.setHealthState(HealthState.OK);
        pet.setHappinessState(HappinessState.SATISFIED);
        pet.setEvolutionState(EvolutionState.BABY);

        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungry(50);
        initialLevels.setEnergy(80);
        initialLevels.setHappy(70);
        initialLevels.setHygiene(90);
        initialLevels.setHealth(100);
        pet.setLevels(initialLevels);

        return petRepo.save(pet);
    }

    public Optional<Pet> getPetById(int id_pet) {
        if (id_pet <= 0) {
            throw new IllegalArgumentException("The id is invalid: " + id_pet);
        }
        Optional<Pet> petOptional = petRepo.findById(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();

            log.info("Pet recuperado: ID={}, EvolutionState={}", pet.getPetId(), pet.getEvolutionState());
            if (pet.getEvolutionState() != null) {
                log.info("EvolutionState name: {}, EvolutionState ordinal: {}",
                        pet.getEvolutionState().name(), pet.getEvolutionState().ordinal());
            } else {
                log.warn("EvolutionState es NULL para Pet ID: {}", pet.getPetId());
            }
        }
        return petOptional;
    }

    @Override
    public List<Pet> getAllPets() {

        List<Pet> allPets = petRepo.findAll();
        if (allPets.isEmpty())
            throw new EmptyPetListException("No pets in the DB.");
        return petRepo.findAll();

    }

    @Override
    public List<Pet> getAllPetsByUserId(Integer userId) {
        return petRepo.findAllByUserId(userId);
    }

    @Override
    public List<Pet> getAllPetsAsAdmin() {
        return petRepo.findAll();
    }

    @Override
    public void deletePet(int id_pet) {

        petRepo.deleteById(id_pet);
    }

    @Override
    @Transactional
    public Pet play(Integer petId, Principal principal) {
        Pet pet = getPetAndVerifyOwnership(petId, principal);
        pet.applyPlayingEffects();
        pet.updateDerivedStates();
        return petRepo.save(pet);
    }

    private Pet getPetAndVerifyOwnership(Integer petId, Principal principal) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Mascota con ID " + petId + " no encontrada."));
        if (!pet.getUser().getUsername().equals(principal.getName())) {
            throw new UnauthorizedUserException("No tienes permiso para interactuar con esta mascota.");
        }
        return pet;
    }

    @Override
    @Transactional
    public Pet feed(Integer petId, Principal principal) {
        Pet pet = getPetAndVerifyOwnership(petId, principal);
        pet.applyFeedingEffects();
        pet.updateDerivedStates();
        return petRepo.save(pet);
    }

    @Override
    @Transactional
    public Pet sleep(Integer petId, Principal principal) {
        Pet pet = getPetAndVerifyOwnership(petId, principal);
        pet.applySleepEffects();
        pet.updateDerivedStates();
        return petRepo.save(pet);
    }


    @Override
    @Transactional
    public Pet giveMeds(Integer petId, Principal principal) {
        Pet pet = getPetAndVerifyOwnership(petId, principal);
        pet.applyMedicineEffects();
        pet.updateDerivedStates();
        return petRepo.save(pet);
    }


    @Override
    @Transactional
    public Pet clean(Integer petId, Principal principal) {
        Pet pet = getPetAndVerifyOwnership(petId, principal);
        pet.applyCleaningEffects();
        pet.updateDerivedStates();
        return petRepo.save(pet);
    }

}
