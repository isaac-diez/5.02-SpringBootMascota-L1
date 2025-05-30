package cat.itacademy.s05.t02.n01.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PetDTO implements Serializable {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String petName;

    @NotNull(message = "El tipo de mascota es obligatorio")
    private PetType petType;

    private Integer id;
    private int happyLevel;
    private EvolutionState evolutionState;

}
