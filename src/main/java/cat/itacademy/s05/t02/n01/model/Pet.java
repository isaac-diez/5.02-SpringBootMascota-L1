package cat.itacademy.s05.t02.n01.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Integer petId;

    private String name;

    @Enumerated(EnumType.STRING)
    private PetType type;

    @Embedded
    private Level levels = new Level();

    @Enumerated(EnumType.STRING)
    private HealthState healthState;

    @Enumerated(EnumType.STRING)
    private HappinessState happinessState;

    @Enumerated(EnumType.STRING)
    private EvolutionState evolutionState;

    private LocalDateTime dob;
    private boolean isSleeping;

    @Column(name="days_old")
    private int daysOld;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonBackReference("user-pets")
    private User user;

    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("pet-events")
    private List<Event> eventList;


    @Embeddable
    @Data
    public static class Level {
        private int hungry;
        private int energy;
        private int happy;
        private int hygiene;
        private int health;

        public void alimentar() {
            this.hungry = Math.max(0, this.hungry - 20);
        }
    }

    public void updateNeeds() {
        this.levels.hungry = Math.min(100, levels.hungry + 5);
        this.levels.energy = Math.max(0, levels.energy - 3);
        if(levels.hungry > 70) this.levels.happy -= 2;
        if(levels.hygiene < 30) this.levels.health -= 1;
    }

//    public void alimentar() {
//        if(this.nivelHambre <= 0) {
//            throw new IllegalStateException("La mascota no tiene hambre");
//        }
//
//        this.nivelHambre = Math.max(0, this.nivelHambre - 20);
//        this.nivelEnergia = Math.min(100, this.nivelEnergia + 20);
//        this.higiene = Math.max(0, this.higiene - 5); // Se ensucia al comer
//
//        if(this.nivelHambre < 30 && this.nivelFelicidad < 50) {
//            this.nivelFelicidad += 10; // Bonus si estaba muy hambrienta
//        }

    public int getDaysOld() {
        return Math.toIntExact(ChronoUnit.DAYS.between(this.dob, LocalDateTime.now()));
    }
}