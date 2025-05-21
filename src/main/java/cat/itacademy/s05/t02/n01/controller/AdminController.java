package cat.itacademy.s05.t02.n01.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to the ADMIN dashboard!";
    }
}
