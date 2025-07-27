package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // This method runs when login happens or JWT is validated
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Step 1: Get user from DB by email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Step 2: Return a Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),            // username
                user.getPassword(),         // encrypted password
                Collections.emptyList()     // authorities (roles), you can update this later
        );
    }
}
 