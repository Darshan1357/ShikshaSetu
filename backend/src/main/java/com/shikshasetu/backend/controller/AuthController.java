package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.AuthRequest;
import com.shikshasetu.backend.dto.AuthResponse;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping(value = "/login", consumes = "application/json")
    public AuthResponse login(@RequestBody AuthRequest request) {

        // 🔐 Authenticate user with Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // ✅ Fetch user details from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔑 Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        System.out.println("Login endpoint hit!");

        return new AuthResponse(token, user.getRole()); 
    }


    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 🛡 Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "User registered successfully";
    }
}
