package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.AuthRequest;
import com.shikshasetu.backend.dto.AuthResponse;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.EmailService;
import com.shikshasetu.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.shikshasetu.backend.dto.RegisterRequest;
import com.shikshasetu.backend.model.Role;

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

    @Autowired
    private EmailService emailService;

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
        String jwtToken = jwtUtil.generateToken(user.getEmail());
        System.out.println("Login endpoint hit!");

        return new AuthResponse(jwtToken, user.getRole()); 
    }
        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setName(request.getName());

        userRepository.save(user);

        // ✅ Send welcome email
        emailService.sendEmail(
        user.getEmail(),
        "🎉 Welcome to ShikshaSetu!",
        "Dear " + user.getName() + ",\n\n" +
        "Welcome to ShikshaSetu – your learning journey starts now!\n" +
        "Explore courses, watch videos, and grow your skills 🚀\n\n" +
        "Regards,\nTeam ShikshaSetu"
    );
    return ResponseEntity.ok("User registered successfully");
    }
}
