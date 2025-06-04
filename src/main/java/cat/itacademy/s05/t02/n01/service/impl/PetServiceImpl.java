package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.exception.PetNotFoundException;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.security.JwtRequestFilter;
import cat.itacademy.s05.t02.n01.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);


    @Autowired
    private PetRepo petRepo;

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void updateLevels(int petId) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet nof found in DB " + petId));

        // Lógica de degradación natural
        pet.updateNeeds();
        verificarConsecuencias(pet);

        petRepo.save(pet);
    }

    private void verificarConsecuencias(Pet pet) {
//        if (pet.getLevels().getHungryLevel() > 90) {
//            pet.aplicarEfecto(new EfectoDesnutricion());
//        }
        // Otras verificaciones...
    }

    public Pet createPet(User user, PetDTO petDto) {

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

}
