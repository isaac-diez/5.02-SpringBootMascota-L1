package cat.itacademy.s05.t02.n01.Repo;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepo extends JpaRepository<Pet, String> {
    Optional<Pet> findById(String id);
}
