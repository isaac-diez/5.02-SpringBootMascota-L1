package cat.itacademy.s05.t02.n01.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

