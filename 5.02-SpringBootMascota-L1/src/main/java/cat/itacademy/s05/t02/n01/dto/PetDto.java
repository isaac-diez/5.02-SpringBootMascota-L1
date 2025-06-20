package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.EvolutionState;
import cat.itacademy.s05.t02.n01.model.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PetDto implements Serializable {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;

    @NotNull(message = "El tipo de mascota es obligatorio")
    private PetType type;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID autogenerado de la mascota, solo presente en respuestas.")
    private Integer petId;
    private int happyLevel;
    private String healthState;
    private EvolutionState evolutionState;
    private String username;

}
