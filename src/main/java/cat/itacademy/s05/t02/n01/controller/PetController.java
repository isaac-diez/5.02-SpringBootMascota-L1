package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.dto.PetResponseDto;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.service.PetService;
import cat.itacademy.s05.t02.n01.service.UserService;
import cat.itacademy.s05.t02.n01.service.impl.PetMapper;
import cat.itacademy.s05.t02.n01.service.impl.PetServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pet")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);


    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/new")
    public ResponseEntity<PetDto> createPet(
            @Valid @RequestBody PetDto petDto,
            Principal principal) { // Spring inyectará el Principal si el usuario está autenticado

        String username = principal.getName();
        User user = userRepo.findByUsername(username) // O un userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Pet createdPet = petService.createPet(user, petDto); // petService.createPet ya acepta un User
        PetDto responseDto = petMapper.toDto(createdPet);
        return ResponseEntity.created(URI.create("/pet/" + createdPet.getPetId()))
                .body(responseDto);
    }

    @GetMapping("/get/{id_pet}")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable int id_pet) {
        log.info("PetController: Attempting to get pet with id_pet: {}", id_pet);
        Optional<Pet> petOptional = petService.getPetById(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            // Mapear Pet a PetResponseDTO
            PetResponseDto dto = new PetResponseDto();
            dto.setPetId(pet.getPetId());
            dto.setName(pet.getName());
            if (pet.getType() != null) dto.setType(PetType.valueOf(pet.getType().name()));
            if (pet.getEvolutionState() != null) dto.setEvolutionState(EvolutionState.valueOf(pet.getEvolutionState().name()));
            if (pet.getUser() != null) dto.setUserId(pet.getUser().getId_user());

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PetDto>> getAllPets() {
        List<Pet> petList = petService.getAllPets();
        if (petList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<PetDto> petDtoList = petList.stream()
                    .map(petMapper::toDto)
                    .toList();
            return ResponseEntity.ok(petDtoList);
        }
    }

}
