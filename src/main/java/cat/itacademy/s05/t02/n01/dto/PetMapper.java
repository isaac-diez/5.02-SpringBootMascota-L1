package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetDto toDto(Pet pet) {
        PetDto dto = new PetDto();
        if (pet.getPetId() != null) {
            dto.setId(pet.getPetId());
        }
        dto.setPetName(pet.getName());
        dto.setPetType(pet.getType());
        dto.setHappyLevel(pet.getLevels().getHappy());
        dto.setEvolutionState(pet.getEvolutionState());
        dto.setUsername(pet.getUser().getUsername());
        return dto;
    }

    public Pet fromDto(PetDto dto) {
        Pet pet = new Pet();
        pet.setName(dto.getPetName());
        pet.setType(dto.getPetType());
        return pet;
    }
}