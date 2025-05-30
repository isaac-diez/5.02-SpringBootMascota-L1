package cat.itacademy.s05.t02.n01.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="events")
@Data
public class Event {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="id_event")
        private int id;

        private EventType type;
        private LocalDateTime date;

        @ManyToOne
        @JoinColumn(name = "id_pet")
        private Pet pet;

        public Event(EventType type) {
                this.type = type;
        }
}
