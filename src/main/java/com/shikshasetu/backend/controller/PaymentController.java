package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.model.Subscription;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    // Mock payment endpoint
    @PostMapping("/subscribe/{courseId}")
    public ResponseEntity<?> subscribeToCourse(@PathVariable Long courseId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Assume ₹1 payment is successful
        Subscription subscription = subscriptionService.createNewSubscription(user, course);

        return ResponseEntity.ok("Payment successful. Subscription active for 7 days.ID: " + subscription.getId());
    }
}
