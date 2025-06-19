package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public PetDto toDto(Pet pet) {
        PetDto dto = new PetDto();
        if (pet.getPetId() != null) {
            dto.setPetId(pet.getPetId());
        }
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setHappyLevel(pet.getLevels().getHappy());
        dto.setEvolutionState(pet.getEvolutionState());
        dto.setUsername(pet.getUser().getUsername());
        return dto;
    }

    public PetDetailResponseDto toDetailDto(Pet pet) {
        if (pet == null) return null;
        PetDetailResponseDto dto = new PetDetailResponseDto();
        dto.setPetId(pet.getPetId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setHealthState(pet.getHealthState());
        dto.setHappinessState(pet.getHappinessState());
        dto.setEvolutionState(pet.getEvolutionState());
        dto.setSleeping(pet.isSleeping());
        dto.setDaysOld(pet.getDaysOld());
        dto.setDob(pet.getDob());

        if (pet.getLevels() != null) {
            LevelDto levelDto = new LevelDto();
            levelDto.setHungry(pet.getLevels().getHungry());
            levelDto.setEnergy(pet.getLevels().getEnergy());
            levelDto.setHappy(pet.getLevels().getHappy());
            levelDto.setHygiene(pet.getLevels().getHygiene());
            levelDto.setHealth(pet.getLevels().getHealth());
            dto.setLevels(levelDto);
        }
        return dto;
    }

    public Pet fromDto(PetDto dto) {
        Pet pet = new Pet();
        pet.setName(dto.getName());
        pet.setType(dto.getType());
        return pet;
    }
}