package cat.itacademy.s05.t02.n01.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LevelDto {
    private int hungry;
    private int energy;
    private int happy;
    private int hygiene;
    private int health;
}