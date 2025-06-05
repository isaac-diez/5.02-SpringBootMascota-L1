package cat.itacademy.s05.t02.n01.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Integer id_user;
    private String username;
    private String role;

}
