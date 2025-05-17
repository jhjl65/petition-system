package com.example.petitions.service;

import com.example.petitions.model.User;
import com.example.petitions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Користувача не знайдено: " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),              // username
                user.getPassword(),           // password (зашифрований)
                Collections.emptyList()       // authorities
        );
    }
}
