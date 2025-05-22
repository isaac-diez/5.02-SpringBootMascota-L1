package cat.itacademy.s05.t02.n01.controller;

import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @PostMapping("/add")
//    public User createUser(@RequestBody User user) {
//        return userService.createUser(user);
//    }
}