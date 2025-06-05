package cat.itacademy.s05.t02.n01.model;

import cat.itacademy.s05.t02.n01.exception.PetNotHungryException;
import cat.itacademy.s05.t02.n01.exception.PetNotSickException;
import cat.itacademy.s05.t02.n01.exception.PetNotTiredEnoughException;
import cat.itacademy.s05.t02.n01.exception.PetTooTiredException;
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

    @Enumerated(EnumType.STRING)
    private HealthState healthState;

    @Enumerated(EnumType.STRING)
    private HappinessState happinessState;

    @Enumerated(EnumType.STRING)
    private EvolutionState evolutionState;

    private LocalDateTime dob;

    private LocalDateTime lastFedTime;
    private LocalDateTime lastPlayedTime;
    private LocalDateTime lastSleptTime;
    private LocalDateTime LastCleanedTime;
    private LocalDateTime lastMedicineGivenTime;

    private boolean isSleeping;
    private boolean isSick;
    private boolean isHungry;
    private boolean isHappy;

    @Column(name="days_old")
    private int daysOld;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonBackReference("user-pets")
    private User user;

    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("pet-events")
    private List<Event> eventList;

    @Embedded
    private Level levels = new Level();

    @Embeddable
    @Data
    public static class Level {
        private int hungry;
        private int energy;
        private int happy;
        private int hygiene;
        private int health;

    }

    public void updateNeeds() {
        this.levels.hungry = Math.min(100, levels.hungry + 5);
        this.levels.energy = Math.max(0, levels.energy - 3);
        if(levels.hungry > 70) this.levels.happy -= 2;
        if(levels.hygiene < 30) this.levels.health -= 1;
    }

    public void applyFeedingEffects() {
        if (this.levels.hungry <= 0) {
            throw new PetNotHungryException("The pet is not hungry");
        }

        this.levels.hungry = Math.max(0, this.levels.hungry - 20);
        this.levels.energy = Math.min(100, this.levels.energy + 20);
        this.levels.hygiene = Math.max(0, this.levels.hygiene - 5);
        this.levels.happy = Math.min(100, this.levels.happy + 10);

        if (this.levels.hungry < 30 && this.levels.happy < 50) {
            this.levels.happy += 10;
        }

        this.lastFedTime = LocalDateTime.now();
    }

    public void applyPlayingEffects() { // Aumenta felicidad, reduce energía.
        if (this.levels.energy < 20) {
            throw new PetTooTiredException("The pet too tired to play");
        }

        this.levels.hungry = Math.min(100, this.levels.hungry + 20);
        this.levels.happy = Math.min(100, this.levels.happy + 30);
        this.levels.energy = Math.max(0, this.levels.energy - 20);
        this.levels.hygiene = Math.max(0, this.levels.hygiene - 10);

        this.lastPlayedTime = LocalDateTime.now();
    }

    public void applyMedicineEffects() {  //Mejora healthState.
        if (!isSick) {
            throw new PetNotSickException("The pet is not sick");
        }

        this.levels.hungry = Math.min(100, this.levels.hungry + 10);
        this.levels.happy = Math.min(100, this.levels.happy + 20);
        this.levels.energy = Math.min(100, this.levels.energy + 20);

        this.lastMedicineGivenTime = LocalDateTime.now();
    }

    public void applySleepEffects() {   // Cambia isSleeping, actualiza lastSleptTime, podría iniciar recuperación de energía.
        if (this.levels.energy > 50) {
            throw new PetNotTiredEnoughException("The pet is not tired enough to sleep");
        }

        this.levels.hungry = Math.min(100, this.levels.hungry + 20);
        this.levels.happy = Math.min(100, this.levels.happy + 20);
        this.levels.energy = Math.min(100, this.levels.energy + 50);
        this.levels.hygiene = Math.max(0, this.levels.hygiene - 10);

        this.lastSleptTime = LocalDateTime.now();
    }

    private static final int HAPPINESS_THRESHOLD_VERY_LOW = 15;
    private static final int HAPPINESS_THRESHOLD_LOW = 35;
    private static final int HAPPINESS_THRESHOLD_HIGH = 65;
    private static final int HAPPINESS_THRESHOLD_VERY_HIGH = 85;

    private static final int HEALTH_THRESHOLD_CRITICAL = 10;
    private static final int HEALTH_THRESHOLD_LOW = 30;
    private static final int HEALTH_THRESHOLD_HIGH = 85;
    private static final int HUNGER_CRITICAL_THRESHOLD = 90;
    private static final int ENERGY_CRITICAL_THRESHOLD = 10;

    private static final int AGE_THRESHOLD_KID = 5;
    private static final int AGE_THRESHOLD_TEENAGER = 10;
    private static final int AGE_THRESHOLD_ADULT = 15;
    private static final int AGE_THRESHOLD_SENIOR = 20;


    public void updateDerivedStates() {

        updateCurrentHealthState();

        checkAndSetIfDeceased();

        if (this.evolutionState == EvolutionState.DEAD) {
            return;
        }

        updateCurrentHappinessState();

        updateCurrentEvolutionStateByAge();
    }

    private void updateCurrentHappinessState() {
        int happy = this.levels.getHappy();
        int hungry = this.levels.getHungry();
        int energy = this.levels.getEnergy();
        HappinessState newHappinessState;

        if (happy <= HAPPINESS_THRESHOLD_VERY_LOW || hungry >= HUNGER_CRITICAL_THRESHOLD || energy <= ENERGY_CRITICAL_THRESHOLD) {
            newHappinessState = HappinessState.MISERABLE;
        } else if (happy <= HAPPINESS_THRESHOLD_LOW) {
            newHappinessState = HappinessState.SAD;
        } else if (happy >= HAPPINESS_THRESHOLD_VERY_HIGH && hungry < (HUNGER_CRITICAL_THRESHOLD / 2) && energy > (ENERGY_CRITICAL_THRESHOLD * 2)) {
            newHappinessState = HappinessState.EXULTANT;
        } else if (happy >= HAPPINESS_THRESHOLD_HIGH) {
            newHappinessState = HappinessState.HAPPY;
        } else {
            newHappinessState = HappinessState.SATISFIED;
        }

        if (this.happinessState != newHappinessState) {
            this.happinessState = newHappinessState;
            // TODO Opcional: registrar un evento de cambio de estado de felicidad
        }
    }

    private void updateCurrentHealthState() {
        int healthLevel = this.levels.getHealth();
        int hungryLevel = this.levels.getHungry();
        HealthState newHealthState;

        if (this.evolutionState == EvolutionState.DEAD) {
            this.healthState = HealthState.DECEASED;
            return;
        }

        if (healthLevel <= HEALTH_THRESHOLD_CRITICAL || hungryLevel >= HUNGER_CRITICAL_THRESHOLD) {
            newHealthState = HealthState.SICK;
        } else if (healthLevel <= HEALTH_THRESHOLD_LOW) {
            newHealthState = HealthState.WEAK;
        } else if (healthLevel >= HEALTH_THRESHOLD_HIGH) {
            newHealthState = HealthState.STRONG; // o FIT
        } else {
            newHealthState = HealthState.OK;
        }

        if (this.healthState != newHealthState) {
            this.healthState = newHealthState;
            // TODO Opcional: registrar un evento de cambio de estado de salud
        }
    }

    private void updateCurrentEvolutionStateByAge() {

        if (this.evolutionState == EvolutionState.DEAD) {
            return;
        }

        EvolutionState newEvolutionState = this.evolutionState;
        if (this.daysOld >= AGE_THRESHOLD_SENIOR && this.evolutionState != EvolutionState.SENIOR) {
            newEvolutionState = EvolutionState.SENIOR;
        } else if (this.daysOld >= AGE_THRESHOLD_ADULT && this.evolutionState.ordinal() < EvolutionState.ADULT.ordinal()) {
            newEvolutionState = EvolutionState.ADULT;
        } else if (this.daysOld >= AGE_THRESHOLD_TEENAGER && this.evolutionState.ordinal() < EvolutionState.TEENAGER.ordinal()) {
            newEvolutionState = EvolutionState.TEENAGER;
        } else if (this.daysOld >= AGE_THRESHOLD_KID && this.evolutionState.ordinal() < EvolutionState.KID.ordinal()) {
            newEvolutionState = EvolutionState.KID;
        } else if (this.evolutionState.ordinal() < EvolutionState.BABY.ordinal()){
            newEvolutionState = EvolutionState.BABY;
        }

        if (this.evolutionState != newEvolutionState) {
            EvolutionState oldEvolutionState = this.evolutionState;
            this.evolutionState = newEvolutionState;
            // TODO Opcional: registrar un evento de evolución (ej. "Tu mascota ha evolucionado de " + oldEvolutionState + " a " + newEvolutionState)
        }
    }

    private void checkAndSetIfDeceased() {

        if (this.levels.getHealth() <= 0 && this.evolutionState != EvolutionState.DEAD) {
            this.evolutionState = EvolutionState.DEAD;
            this.healthState = HealthState.SICK;
            this.levels.setHungry(0);
            this.levels.setEnergy(0);
            this.levels.setHappy(0);
            this.isSleeping = false;

        }
    }

    public int getDaysOld() {
        return Math.toIntExact(ChronoUnit.DAYS.between(this.dob, LocalDateTime.now()));
    }
}