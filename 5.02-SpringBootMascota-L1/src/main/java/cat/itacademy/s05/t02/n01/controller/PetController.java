package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.dto.PetDetailResponseDto;
import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.dto.PetResponseDto;
import cat.itacademy.s05.t02.n01.model.*;
import cat.itacademy.s05.t02.n01.service.PetService;
import cat.itacademy.s05.t02.n01.service.UserService;
import cat.itacademy.s05.t02.n01.dto.PetMapper;
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
import java.util.stream.Collectors;

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
    public ResponseEntity<PetDetailResponseDto> getPetById(@PathVariable int id_pet) {
        log.info("PetController: Attempting to get pet with id_pet: {}", id_pet);
        Optional<Pet> petOptional = petService.getPetById(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
//            PetDetailResponseDto dto = new PetDetailResponseDto();
//            dto.setPetId(pet.getPetId());
//            dto.setName(pet.getName());
//            if (pet.getType() != null) dto.setType(PetType.valueOf(pet.getType().name()));
//            if (pet.getEvolutionState() != null) dto.setEvolutionState(EvolutionState.valueOf(pet.getEvolutionState().name()));

            return ResponseEntity.ok(petMapper.toDetailDto(pet));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-pets") // Ruta descriptiva y no conflictiva
    public ResponseEntity<List<PetDto>> getMyPets(Principal principal) {
        // Obtenemos el usuario autenticado a partir del token
        String username = principal.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Llamamos al servicio para obtener las mascotas de ESE usuario
        List<Pet> petList = petService.getAllPetsByUserId(user.getId());

        if (petList.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            // Mapeamos la lista de entidades a una lista de DTOs para la respuesta
            List<PetDto> petDtoList = petList.stream()
                    .map(petMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(petDtoList);
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

    @PostMapping("/{id_pet}/play")
    public ResponseEntity<PetDetailResponseDto> playWithPet(@PathVariable int id_pet, Principal principal) {
        Pet updatedPet = petService.play(id_pet, principal);
        return ResponseEntity.ok(petMapper.toDetailDto(updatedPet));
    }


    @PostMapping("/{id_pet}/feed")
    public ResponseEntity<PetDetailResponseDto> feedPet(@PathVariable int id_pet) {
        log.info("PetController: Attempting to get pet to feed with id_pet: {}", id_pet);
        Optional<Pet> petOptional = petService.feed(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            return ResponseEntity.ok(petMapper.toDetailDto(pet));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id_pet}/sleep")
    public ResponseEntity<PetDetailResponseDto> getPetToSleep(@PathVariable int id_pet) {
        log.info("PetController: Attempting to get pet to sleep with id_pet: {}", id_pet);
        Optional<Pet> petOptional = petService.sleep(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            return ResponseEntity.ok(petMapper.toDetailDto(pet));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id_pet}/meds")
    public ResponseEntity<PetDetailResponseDto> giveMedsToPet(@PathVariable int id_pet) {
        log.info("PetController: Attempting to give meds to pet with id_pet: {}", id_pet);
        Optional<Pet> petOptional = petService.giveMeds(id_pet);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            return ResponseEntity.ok(petMapper.toDetailDto(pet));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
