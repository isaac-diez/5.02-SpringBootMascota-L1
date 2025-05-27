package cat.itacademy.s05.t02.n01.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;
    private PetType petType;

    @Embedded
    private Level levels = new Level();

    private HealthState healthState;
    private HappinessState happinessState;
    private EvolutionState evolutionState;

    private LocalDateTime dob;
    private boolean isSleeping;
    private int daysOld;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "pet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Event> eventList;


    @Embeddable
    @Data
    public static class Level {
        private int hungryLevel;
        private int energyLevel;
        private int happyLevel;
        private int hygieneLevel;
        private int healthLevel;

        public void alimentar() {
            this.hungryLevel = Math.max(0, this.hungryLevel - 20);
        }
    }

    public void updateNeeds() {
        this.levels.hungryLevel = Math.min(100, levels.hungryLevel + 5);
        this.levels.energyLevel = Math.max(0, levels.energyLevel - 3);
        if(levels.hungryLevel > 70) this.levels.happyLevel -= 2;
        if(levels.hygieneLevel < 30) this.levels.healthLevel -= 1;
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




}
