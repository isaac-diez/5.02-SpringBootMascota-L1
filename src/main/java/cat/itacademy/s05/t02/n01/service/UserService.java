package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.exception.*;
import cat.itacademy.s05.t02.n01.model.Role;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userDetailsService")
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        var roles = new ArrayList<GrantedAuthority>();

        for (Role role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getRoleType()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
    }

    public UserDetails loadUserById(int id) throws UsernameNotFoundException {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));

        var roles = new ArrayList<GrantedAuthority>();

        for (Role role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getRoleType()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
    }

    public User findUserById(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public User createUser(String username, String rawPassword, List<String> roleNames) {

        User existingUser = userRepo.findByUsername(username);

        if (existingUser != null) {
            throw new UsernameAlreadyInDataBaseException(username);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Set<Role> roles = roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleType(roleName);
                    role.setUser(user);
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userRepo.save(user);
    }
}
