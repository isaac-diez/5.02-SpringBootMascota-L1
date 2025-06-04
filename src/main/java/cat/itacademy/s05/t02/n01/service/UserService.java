package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.exception.*;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userDetailsService")
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException(username));

        var role = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singleton(role));
    }

    public UserDetails loadUserById(int id) throws UsernameNotFoundException {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));

        var role = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singleton(role));
    }

    public User findUserById(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public User createUser(String username, String rawPassword) {

        if (userRepo.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyInDataBaseException("Username " + username + " already exists.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole("USER");

        return userRepo.save(user);
    }

    public User createUserWithRole(String username, String rawPassword, String role) {

        if (userRepo.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyInDataBaseException("Username " + username + " already exists.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(role);

        return userRepo.save(user);
    }
}
