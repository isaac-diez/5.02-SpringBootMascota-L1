package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.Event;
import cat.itacademy.s05.t02.n01.model.EvolutionState;
import cat.itacademy.s05.t02.n01.model.PetType;
import lombok.Data;

import java.util.List;

@Data
public class PetResponseDto {
    private Integer petId;
    private String name;
    private PetType type;
    private EvolutionState evolutionState;
    private Integer userId;
    private String username;
    private List<Event> eventList;
}