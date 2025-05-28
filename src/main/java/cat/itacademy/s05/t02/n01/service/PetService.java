package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;

import cat.itacademy.s05.t02.n01.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface PetService {
    public Pet createPet(User user, PetDTO petDto);
}
