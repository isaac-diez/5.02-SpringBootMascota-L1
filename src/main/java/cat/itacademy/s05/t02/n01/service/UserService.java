package cat.itacademy.s05.t02.n01.service;

import cat.itacademy.s05.t02.n01.Repo.UserRepo;
import cat.itacademy.s05.t02.n01.model.Role;
import cat.itacademy.s05.t02.n01.model.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public User createUser(String username, String rawPassword, List<String> roleNames) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        List<Role> roles = roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleType(roleName);
                    role.setUser(user); // relación inversa
                    return role;
                })
                .collect(Collectors.toList());

        user.setRoles(roles);

        return userRepo.save(user);
    }
}
