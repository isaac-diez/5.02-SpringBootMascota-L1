package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.dto.PetDto;

import cat.itacademy.s05.t02.n01.model.User;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface PetService {
    public Pet createPet(User user, PetDto petDto);
    public Optional<Pet> getPetById(int id_pet);
    public List<Pet> getAllPets();
    List<Pet> getAllPetsByUserId(Integer userId);
    List<Pet> getAllPetsAsAdmin();
    public Pet play(Integer idPet, Principal principal);
    public Optional<Pet> feed(int idPet);
    public Optional<Pet> sleep(int idPet);
    public Optional<Pet> giveMeds(int idPet);
}
