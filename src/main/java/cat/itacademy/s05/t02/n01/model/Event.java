package cat.itacademy.s05.t02.n01.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
        @JoinColumn(name = "pet_id")
        @JsonBackReference("pet-events")
        private Pet pet;

        public Event(EventType type, LocalDateTime date) {
                this.type = type;
                this.date = date;
        }
}
