package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.Repo.PetRepo;
import cat.itacademy.s05.t02.n01.exception.PetNotFoundException;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepo petRepo;

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void updateLevels(String petId) {
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

    public PetDTO convertToDto(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setPetName(pet.getName());

        return dto;
    }

    public Pet createPet(User user, PetDTO petDto) {
        // Validación básica
        if (user == null || petDto == null) {
            throw new IllegalArgumentException("User and PetDTO can't be null");
        }

        // Crear nueva mascota desde el DTO
        Pet pet = new Pet();
        pet.setName(petDto.getPetName());
        pet.setPetType(petDto.getPetType());
        pet.setUser(user);
        pet.setDob(LocalDateTime.now());
        pet.setDaysOld(0);
        pet.setHealthState(HealthState.OK);
        pet.setHappinessState(HappinessState.SATISFIED);
        pet.setEvolutionState(EvolutionState.BABY);

        // Inicializar niveles
        Pet.Level initialLevels = new Pet.Level();
        initialLevels.setHungryLevel(50);
        initialLevels.setEnergyLevel(80);
        initialLevels.setHappyLevel(70);
        initialLevels.setHygieneLevel(90);
        initialLevels.setHealthLevel(100);
        pet.setLevels(initialLevels);

        // Guardar en base de datos
        return petRepo.save(pet);
    }

}
