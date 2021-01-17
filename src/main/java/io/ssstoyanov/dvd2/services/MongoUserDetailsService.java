package io.ssstoyanov.dvd2.services;

import io.ssstoyanov.dvd2.entities.User;
import io.ssstoyanov.dvd2.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MongoUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MongoUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User u = user.get();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }
}