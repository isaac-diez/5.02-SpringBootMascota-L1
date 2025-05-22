package cat.itacademy.s05.t02.n01.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

    @Entity
    @Data
    @Table(name="roles")
    public class Role {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id_role;

        @NotBlank
        private String roleType;

        @ManyToOne
        @JoinColumn(name = "id_user")
        private User user;

}
