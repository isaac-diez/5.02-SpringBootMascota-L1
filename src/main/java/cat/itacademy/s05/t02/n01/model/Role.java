package cat.itacademy.s05.t02.n01.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
    @Table(name="roles")
    public class Role {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id_role;

        @NotBlank
        private String roleType;

        @ManyToOne
        @JoinColumn(name = "id_user")
        @JsonBackReference
        private User user;
}
