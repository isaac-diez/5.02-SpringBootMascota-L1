package cat.itacademy.s05.t02.n01.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @GetMapping("/hello")
    public String helloUser() {
        return "Hello USER!";
    }

}