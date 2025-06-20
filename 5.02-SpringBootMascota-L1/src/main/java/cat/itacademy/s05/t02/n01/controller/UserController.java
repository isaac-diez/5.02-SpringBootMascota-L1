package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.dto.RegisterDto;
import cat.itacademy.s05.t02.n01.dto.UserDto;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.UserService;
import cat.itacademy.s05.t02.n01.dto.UserMapper;
import cat.itacademy.s05.t02.n01.service.impl.PetServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);


    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/hello")
    public String helloUser() {
        return "Hello USER!";
    }

    @PostMapping("/new")
    public User createUser(@RequestBody RegisterDto registerDto) {
        return userService.createUser(registerDto.getUsername(), registerDto.getPassword());
    }

//    @GetMapping("/getAll")
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<User> userList = userService.getAllUsers();
//        if (userList.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        } else {
//            List<UserDto> userDtoList = userList.stream()
//                    .map(userMapper::toDto)
//                    .toList();
//            return ResponseEntity.ok(userDtoList);
//        }
//    }


    @DeleteMapping("delete/{id_user}")
    public ResponseEntity<Void> deletePet(@PathVariable int id_user) {
        log.info("PetController: Attempting to delete user with id: {}", id_user);
        userService.deleteUser(id_user);
        return ResponseEntity.ok().build();
    }

}