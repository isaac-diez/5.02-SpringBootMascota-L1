package cat.itacademy.s05.t02.n01.service.impl;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetDTO toDto(Pet pet) {
        PetDTO dto = new PetDTO();
        if (pet.getPetId() != null) {
            dto.setId(pet.getPetId());
        }
        dto.setPetName(pet.getName());
        dto.setPetType(pet.getType());
//        dto.setHappyLevel(pet.getLevels().getHappyLevel());
//        dto.setEvolutionState(pet.getEvolutionState());
        return dto;
    }

    public Pet fromDto(PetDTO dto) {
        Pet pet = new Pet();
        pet.setName(dto.getPetName());
        pet.setType(dto.getPetType());
        return pet;
    }
}