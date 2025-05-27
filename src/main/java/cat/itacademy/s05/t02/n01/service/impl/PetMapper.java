package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetDTO toDto(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setPetName(pet.getName());
        dto.setPetType(pet.getPetType());
//        dto.setHappyLevel(pet.getLevels().getHappyLevel());
//        dto.setEvolutionState(pet.getEvolutionState());
        return dto;
    }

    public Pet fromDto(PetDTO dto) {
        Pet pet = new Pet();
        pet.setName(dto.getPetName());
        pet.setPetType(dto.getPetType());
        return pet;
    }
}