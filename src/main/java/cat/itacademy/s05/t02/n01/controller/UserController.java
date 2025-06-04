package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.dto.RegisterDto;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/hello")
    public String helloUser() {
        return "Hello USER!";
    }

    @PostMapping("/new")
    public User createUser(@RequestBody RegisterDto registerDto) {
        return userService.createUser(registerDto.getUsername(), registerDto.getPassword());
    }

}