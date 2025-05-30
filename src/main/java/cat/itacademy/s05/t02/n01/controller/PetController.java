package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.PetDTO;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.PetService;
import cat.itacademy.s05.t02.n01.service.UserService;
import cat.itacademy.s05.t02.n01.service.impl.PetMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetMapper petMapper;

    @PostMapping
    public ResponseEntity<PetDTO> createPet(
            @RequestHeader("userId") int userId,
            @Valid @RequestBody PetDTO petDto) {

        User user = userService.findUserById(userId);

        Pet createdPet = petService.createPet(user, petDto);
        PetDTO responseDto = petMapper.toDto(createdPet);

        return ResponseEntity.created(URI.create("/pets/" + createdPet.getPetId()))
                .body(responseDto);
    }


}
