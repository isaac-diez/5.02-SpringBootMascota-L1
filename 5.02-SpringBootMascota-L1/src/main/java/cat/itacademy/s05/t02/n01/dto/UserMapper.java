package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        if (user.getId_user() != null) {
            dto.setId_user(user.getId_user());
        }
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());

        return dto;
    }

    public User fromDto(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        return user;
    }
}