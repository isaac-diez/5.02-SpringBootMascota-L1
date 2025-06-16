package cat.itacademy.s05.t02.n01.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to the ADMIN dashboard!";
    }
}
