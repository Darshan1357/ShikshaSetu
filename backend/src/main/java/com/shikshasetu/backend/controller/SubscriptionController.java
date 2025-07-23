package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Subscription;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.service.SubscriptionService;
import com.shikshasetu.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    // 📌 Subscribe to 1-week plan (₹1/week)
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // 📅 Create new 7-day subscription
        Subscription subscription = subscriptionService.createNewSubscription(user);
        return ResponseEntity.ok("Subscription created till: " + subscription.getEndDate());
    }
    // 📌 Check if subscription is active
    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        boolean active = subscriptionService.hasActiveSubscription(user);
        return ResponseEntity.ok("Subscription active: " + active);
    }
}
