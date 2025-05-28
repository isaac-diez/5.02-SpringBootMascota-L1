package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
    public class UserDomainService {

        private final UserRepo userRepo;

        public UserDomainService(UserRepo userRepo) {
            this.userRepo = userRepo;
        }

        public User findUserByUsername(String username) {
            User user = userRepo.findByUsername(username);

            if (user == null) {
                throw new EntityNotFoundException("Usuario no encontrado");
            }

            return user;
    }

}
