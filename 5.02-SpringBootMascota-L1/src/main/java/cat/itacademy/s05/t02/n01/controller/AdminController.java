package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.dto.PetDto;
import cat.itacademy.s05.t02.n01.dto.PetMapper;
import cat.itacademy.s05.t02.n01.dto.UserDto;
import cat.itacademy.s05.t02.n01.dto.UserMapper;
import cat.itacademy.s05.t02.n01.model.Pet;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.PetService;
import cat.itacademy.s05.t02.n01.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private PetService petService;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to the ADMIN dashboard!";
    }

    @GetMapping("/pets/all")
    public ResponseEntity<List<PetDto>> getAllPetsForAdmin() {
        List<Pet> allPets = petService.getAllPetsAsAdmin();

        if (allPets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<PetDto> allPetsDto = allPets.stream()
                .map(petMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(allPetsDto);
    }

    @GetMapping("users/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        if (userList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<UserDto> userDtoList = userList.stream()
                    .map(userMapper::toDto)
                    .toList();
            return ResponseEntity.ok(userDtoList);
        }
    }
}
