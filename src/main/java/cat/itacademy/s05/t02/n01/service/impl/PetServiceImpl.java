package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.exception.EmptyPetListException;
import cat.itacademy.s05.t02.n01.exception.PetNotFoundException;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.service.PetService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);


    @Autowired
    private PetRepo petRepo;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateAllPetsPassiveStates() {
        log.info("Ejecutando tarea programada: Actualizando estados pasivos de las mascotas...");

        // 1. Obtener todas las mascotas que necesitan ser actualizadas.
        //    Para empezar, podemos obtener todas las que no estén muertas.
        List<Pet> activePets = petRepo.findAllByEvolutionStateNot(EvolutionState.DEAD);

        if (activePets.isEmpty()) {
            log.info("No hay mascotas activas para actualizar.");
            return;
        }

        // 2. Iterar sobre cada mascota y aplicar la lógica de degradación.
        for (Pet pet : activePets) {
            try {
                log.trace("Actualizando mascota con ID: {}", pet.getPetId());

                // La lógica que ya tienes, ahora aplicada a cada mascota en el bucle.
                pet.updateNeeds();         // Degrada los niveles (hambre, energía, etc.).
                pet.updateDerivedStates(); // Actualiza los enums (HappinessState, HealthState, etc.).

                // 3. Guardar la mascota actualizada.
                //    Como el method es @Transactional, los cambios en las entidades gestionadas
                //    podrían guardarse al final de la transacción sin una llamada explícita a save().
                //    Sin embargo, llamar a save() es más explícito y no hace daño.
                petRepo.save(pet);

            } catch (Exception e) {
                // Captura de errores para una mascota específica para no detener todo el proceso.
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
        pet.setName(petDto.getPetName());
        pet.setType(petDto.getPetType());
        pet.setUser(user);
        pet.setDob(LocalDateTime.now());
        pet.setDaysOld(0);
        pet.setHealthState(HealthState.OK);
        pet.setHappinessState(HappinessState.SATISFIED);
        pet.setEvolutionState(EvolutionState.BABY);

        // Inicializar niveles
        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungry(50);
        initialLevels.setEnergy(80);
        initialLevels.setHappy(70);
        initialLevels.setHygiene(90);
        initialLevels.setHealth(100);
        pet.setLevels(initialLevels);

        // Guardar en base de datos
        return petRepo.save(pet);
    }

    public Optional<Pet> getPetById(int id_pet) {
        if (id_pet <= 0) { // Mejorado para incluir IDs no positivos
            throw new IllegalArgumentException("The id is invalid: " + id_pet);
        }
        Optional<Pet> petOptional = petRepo.findById(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            // Log para inspeccionar
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
    public Optional<Pet> play(int id_pet) {
        Optional<Pet> petOptional = getPetById(id_pet);
        Pet pet = new Pet();
        if (petOptional.isPresent()) {
            pet = petOptional.get();
        } else {
            throw new PetNotFoundException("The Pet with id" + id_pet + "is not found in the DB");
        }

        pet.applyPlayingEffects();

        petRepo.save(pet);

        return petRepo.findById(id_pet);

    }

    @Override
    public Optional<Pet> feed(int id_pet) {
        Optional<Pet> petOptional = getPetById(id_pet);
        Pet pet = new Pet();
        if (petOptional.isPresent()) {
            pet = petOptional.get();
        } else {
            throw new PetNotFoundException("The Pet with id" + id_pet + "is not found in the DB");
        }

        pet.applyFeedingEffects();

        petRepo.save(pet);

        return petRepo.findById(id_pet);

    }

    @Override
    public Optional<Pet> sleep(int id_pet) {
        Optional<Pet> petOptional = getPetById(id_pet);
        Pet pet = new Pet();
        if (petOptional.isPresent()) {
            pet = petOptional.get();
        } else {
            throw new PetNotFoundException("The Pet with id" + id_pet + "is not found in the DB");
        }

        pet.applySleepEffects();

        petRepo.save(pet);

        return petRepo.findById(id_pet);

    }

}
