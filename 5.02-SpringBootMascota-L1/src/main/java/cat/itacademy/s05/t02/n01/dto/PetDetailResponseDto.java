package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PetDetailResponseDto {
    private Integer petId;
    private String name;
    private PetType type;
    private HealthState healthState;
    private HappinessState happinessState;
    private EvolutionState evolutionState;
    private boolean isSleeping;
    private int daysOld;
    private LocalDateTime dob;
    private Pet.Level levels; // Incluye el objeto Level completo
}