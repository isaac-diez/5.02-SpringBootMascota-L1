package cat.itacademy.s05.t02.n01.model;

import cat.itacademy.s05.t02.n01.exception.*;
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

    @Transient
    private LocalDateTime lastFedTime;
    @Transient
    private LocalDateTime lastPlayedTime;
    @Transient
    private LocalDateTime lastSleptTime;
    @Transient
    private LocalDateTime LastCleanedTime;
    @Transient
    private LocalDateTime LastMedGivenTime;

    private boolean isSleeping;
    @Transient
    private boolean isSick;
    @Transient
    private boolean isHungry;
    @Transient
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

    private static final int BASE_PASSIVE_HUNGER_INCREASE = 5;
    private static final int BASE_PASSIVE_ENERGY_DECREASE = 4;
    private static final int BASE_PASSIVE_HYGIENE_DECREASE = 2;

    private static final int HUNGER_THRESHOLD_HIGH = 50;
    private static final int HUNGER_THRESHOLD_CRITICAL = 80;
    private static final int HYGIENE_THRESHOLD_LOW = 40;
    private static final int HYGIENE_THRESHOLD_CRITICAL = 20;
    private static final int ENERGY_THRESHOLD_LOW = 30;
    private static final int ENERGY_THRESHOLD_CRITICAL = 15;

    private static final int HAPPINESS_THRESHOLD_VERY_LOW = 15;
    private static final int HAPPINESS_THRESHOLD_LOW = 35;
    private static final int HAPPINESS_THRESHOLD_HIGH = 65;
    private static final int HAPPINESS_THRESHOLD_VERY_HIGH = 85;

    private static final int HEALTH_THRESHOLD_CRITICAL = 10;
    private static final int HEALTH_THRESHOLD_LOW = 30;
    private static final int HEALTH_THRESHOLD_HIGH = 65;
    private static final int HEALTH_THRESHOLD_VERY_HIGH = 85;

    private static final int AGE_THRESHOLD_KID = 5;
    private static final int AGE_THRESHOLD_TEENAGER = 10;
    private static final int AGE_THRESHOLD_ADULT = 15;
    private static final int AGE_THRESHOLD_SENIOR = 20;

    // Modificadores y bonificaciones
    private static final double SICKNESS_PENALTY_MULTIPLIER = 1.5;
    public void updateNeeds() {
        // Si la mascota está muerta, no necesita actualizar sus necesidades.
        if (this.evolutionState == EvolutionState.DEAD) {
            return;
        }

        // --- FASE 1: CÁLCULO DE TASAS Y BONIFICACIONES ---
        // Las tasas de cambio pueden variar según el estado actual de la mascota.

        // Modificadores basados en la evolución
        double hungerRateModifier = 1.0;
        double energyRateModifier = 1.0;
        switch (this.evolutionState) {
            case BABY:
                hungerRateModifier = 1.5; // Los bebés tienen más hambre
                break;
            case ADULT:
                hungerRateModifier = 0.8; // Los adultos aguantan más
                break;
            case SENIOR:
                energyRateModifier = 1.2; // Los ancianos se cansan más rápido
                break;
        }

        int currentHungerIncrease = (int) (BASE_PASSIVE_HUNGER_INCREASE * hungerRateModifier);
        int currentEnergyDecrease = (int) (BASE_PASSIVE_ENERGY_DECREASE * energyRateModifier);
        int currentHygieneDecrease = BASE_PASSIVE_HYGIENE_DECREASE;
        int naturalHygieneRecovery = 0;

        // Bonificación por estar "Saludable y Feliz"
        if (this.levels.getHealth() > HEALTH_THRESHOLD_VERY_HIGH && this.levels.getHappy() > HAPPINESS_THRESHOLD_VERY_HIGH) {
            // La energía disminuye más lentamente
            currentEnergyDecrease = (int) (currentEnergyDecrease * 0.5); // 50% más lento
        }

        // Bonificación por "Recuperación Natural"
        if (this.levels.getHappy() > HAPPINESS_THRESHOLD_VERY_HIGH && this.levels.getHygiene() < (HYGIENE_THRESHOLD_LOW + 10)) {
            // Si está muy feliz pero un poco sucio, tiene una pequeña recuperación de higiene
            naturalHygieneRecovery = 1;
        }


        // --- FASE 2: DEGRADACIÓN PASIVA BASE ---
        // Se aplican los cambios básicos calculados en la Fase 1.

        this.levels.setHungry(this.levels.getHungry() + currentHungerIncrease);
        this.levels.setHygiene(this.levels.getHygiene() - currentHygieneDecrease + naturalHygieneRecovery);

        if (!isSleeping) {
            this.levels.setEnergy(this.levels.getEnergy() - currentEnergyDecrease);
        }


        // --- FASE 3: CÁLCULO DE PENALIZACIONES (EFECTOS EN CASCADA Y COMBINADOS) ---
        int happinessPenalty = 0;
        int healthPenalty = 0;

        // Penalización por HAMBRE ALTA
        if (this.levels.getHungry() > HUNGER_THRESHOLD_CRITICAL) {
            happinessPenalty += 15;
            healthPenalty += 5;
        } else if (this.levels.getHungry() > HUNGER_THRESHOLD_HIGH) {
            happinessPenalty += 5;
        }

        // Penalización por HIGIENE BAJA
        if (this.levels.getHygiene() < HYGIENE_THRESHOLD_CRITICAL) {
            happinessPenalty += 10;
            healthPenalty += 15;
        } else if (this.levels.getHygiene() < HYGIENE_THRESHOLD_LOW) {
            healthPenalty += 5;
        }

        // Penalización por ENERGÍA BAJA
        if (this.levels.getEnergy() < ENERGY_THRESHOLD_CRITICAL) {
            happinessPenalty += 8;
            healthPenalty += 8;
        } else if (this.levels.getEnergy() < ENERGY_THRESHOLD_LOW) {
            happinessPenalty += 3;
        }

        // *** EFECTOS COMBINADOS ***

        if (this.levels.getHungry() > HUNGER_THRESHOLD_HIGH && this.levels.getHygiene() < HYGIENE_THRESHOLD_LOW) {
            healthPenalty += 20;
            happinessPenalty += 30;
        }

        if (this.levels.getHappy() < HAPPINESS_THRESHOLD_LOW && this.levels.getEnergy() < ENERGY_THRESHOLD_LOW) {
            healthPenalty += 3;
        }

        if (this.levels.getHygiene() < HYGIENE_THRESHOLD_LOW && this.levels.getHungry() > HUNGER_THRESHOLD_HIGH && this.levels.getEnergy() < ENERGY_THRESHOLD_LOW) {
            healthPenalty += 25;
        }

        // Modificador por estar ENFERMO
        // Si la mascota está enferma (determinado por su HealthState), las penalizaciones son mayores.
        if (this.healthState == HealthState.SICK) {
            happinessPenalty = (int) (happinessPenalty * SICKNESS_PENALTY_MULTIPLIER);
            healthPenalty = (int) (healthPenalty * SICKNESS_PENALTY_MULTIPLIER);
        }


        // --- FASE 4: APLICACIÓN DE CAMBIOS Y LÍMITES ---
        // Se aplican las penalizaciones y se asegura que los valores se mantengan en el rango [0, 100].

        this.levels.setHappy(Math.max(0, this.levels.getHappy() - happinessPenalty));
        this.levels.setHealth(Math.max(0, this.levels.getHealth() - healthPenalty));

        this.levels.setHungry(Math.min(100, this.levels.getHungry()));
        this.levels.setHygiene(Math.max(0, Math.min(100, this.levels.getHygiene()))); // La higiene también tiene un máximo de 100
        this.levels.setEnergy(Math.max(0, this.levels.getEnergy()));
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

        this.isSleeping = false;

        this.lastFedTime = LocalDateTime.now();
    }

    public void applyPlayingEffects() { // Aumenta felicidad, reduce energía.
        if (this.levels.energy < 20) {
            throw new PetTooTiredException("The pet too tired to play");
        }

        this.levels.happy = Math.min(100, this.levels.hungry + 35);
        this.levels.hungry = Math.min(100, this.levels.hungry + 20);
        this.levels.happy = Math.min(100, this.levels.happy + 30);
        this.levels.energy = Math.max(0, this.levels.energy - 20);
        this.levels.hygiene = Math.max(0, this.levels.hygiene - 10);

        this.isSleeping = false;

        this.lastPlayedTime = LocalDateTime.now();
    }

    public void applyMedicineEffects() {
        if (!isSick) {
            throw new PetNotSickException("The pet is not sick");
        }

        this.levels.happy = Math.min(100, this.levels.hungry + 25);
        this.levels.hungry = Math.min(100, this.levels.hungry + 10);
        this.levels.happy = Math.min(100, this.levels.happy + 20);
        this.levels.energy = Math.min(100, this.levels.energy + 20);

        this.isSleeping = false;

        this.LastMedGivenTime = LocalDateTime.now();
    }

    public void applySleepEffects() {
        if (this.levels.energy > 50) {
            throw new PetNotTiredEnoughException("The pet is not tired enough to sleep");
        }

        this.levels.happy = Math.min(100, this.levels.hungry + 20);
        this.levels.hungry = Math.min(100, this.levels.hungry + 20);
        this.levels.happy = Math.min(100, this.levels.happy + 20);
        this.levels.energy = Math.min(100, this.levels.energy + 50);
        this.levels.hygiene = Math.max(0, this.levels.hygiene - 10);

        this.isSleeping = true;

        this.lastSleptTime = LocalDateTime.now();
    }

    public void applyCleaningEffects() {
        if (this.levels.hygiene == 100) {
            throw new PetNotDirtyEnoughException("The pet is not dirty enough");
        }

        this.levels.happy = Math.min(100, this.levels.hungry + 30);
        this.levels.hungry = Math.min(100, this.levels.hungry + 10);
        this.levels.happy = Math.min(100, this.levels.happy + 15);
        this.levels.energy = Math.min(100, this.levels.energy + 20);
        this.levels.hygiene = Math.min(100, this.levels.hygiene + 50);

        this.isSleeping = false;

        this.LastCleanedTime = LocalDateTime.now();
    }

    public void updateDerivedStates() {

        updateCurrentHealthState();

        checkAndSetIfDeceased();

        if (this.evolutionState == EvolutionState.DEAD) {
            return;
        }

        updateCurrentHappinessState();

        updateCurrentEvolutionStateByAge();
    }

    private void updateCurrentHungerState() {

        this.isHungry = (this.levels.getHungry() <= 50);
    }

    private void updateCurrentHappinessState() {
        int happy = this.levels.getHappy();
        int hungry = this.levels.getHungry();
        int energy = this.levels.getEnergy();

        if (happy <= HAPPINESS_THRESHOLD_VERY_LOW || hungry >= HUNGER_THRESHOLD_CRITICAL || energy <= ENERGY_THRESHOLD_CRITICAL) {
            this.happinessState = HappinessState.MISERABLE;
        } else if (happy <= HAPPINESS_THRESHOLD_LOW) {
            this.happinessState = HappinessState.SAD;
        } else if (happy >= HAPPINESS_THRESHOLD_VERY_HIGH && hungry < (HUNGER_THRESHOLD_CRITICAL / 2) && energy > (ENERGY_THRESHOLD_CRITICAL * 2)) {
            this.happinessState = HappinessState.EXULTANT;
        } else if (happy >= HAPPINESS_THRESHOLD_HIGH) {
            this.happinessState = HappinessState.HAPPY;
        } else {
            this.happinessState = HappinessState.SATISFIED;
        }

        this.isHappy = (this.levels.happy >= 50);

//        if (this.happinessState != newHappinessState) {
//            this.happinessState = newHappinessState;
//            // TODO Opcional: registrar un evento de cambio de estado de felicidad
//        }
    }

    private void updateCurrentHealthState() {
        int healthLevel = this.levels.getHealth();
        int hungryLevel = this.levels.getHungry();

        if (this.evolutionState == EvolutionState.DEAD) {
            this.healthState = HealthState.DEAD;

            return;
        }

        if (healthLevel <= HEALTH_THRESHOLD_CRITICAL || hungryLevel >= HUNGER_THRESHOLD_CRITICAL) {
            this.healthState = HealthState.SICK;
        } else if (healthLevel <= HEALTH_THRESHOLD_LOW) {
            this.healthState = HealthState.WEAK;
        } else if (healthLevel >= HEALTH_THRESHOLD_HIGH) {
            this.healthState = HealthState.STRONG;
        } else if (healthLevel >= HEALTH_THRESHOLD_VERY_HIGH) {
            this.healthState = HealthState.FIT;
        } else {
            this.healthState = HealthState.OK;
        }

        this.isSick = (this.healthState == HealthState.SICK);

//        if (this.healthState != this.healthState) {
//            this.healthState = this.healthState;
//            // TODO Opcional: registrar un evento de cambio de estado de salud
//        }
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
            this.healthState = HealthState.DEAD;
            this.levels.setHealth(0);
            this.levels.setHungry(0);
            this.levels.setEnergy(0);
            this.levels.setHygiene(0);
            this.isSleeping = false;

        }
    }

    public int getDaysOld() {
        return this.daysOld = Math.toIntExact(ChronoUnit.HOURS.between(this.dob, LocalDateTime.now()));
    }
}