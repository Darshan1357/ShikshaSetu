package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.Subscription;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailService emailService;

    public boolean hasActiveSubscription(User user) {
        return subscriptionRepository.existsByUserAndActiveTrue(user);
    }

    public Subscription createNewSubscription(User user) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        Subscription subscription = Subscription.builder()
                .user(user)
                .startDate(today)
                .endDate(endDate)
                .active(true)
                .build();
        return subscriptionRepository.save(subscription);
    }

    public void expireOldSubscriptions() {
        subscriptionRepository.findAll().forEach(subscription -> {
            if (subscription.getEndDate().isBefore(LocalDate.now())) {
                subscription.setActive(false);
                subscriptionRepository.save(subscription);
            }
        });
    }

    public boolean hasActiveSubscription(User user, Course course) {
    return subscriptionRepository.existsByUserAndCourseAndActiveTrue(user, course);
    }
    @Scheduled(cron = "0 0 12 * * ?") // Runs every day at noon
    public void sendSubscriptionReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    var expiringSoon = subscriptionRepository.findAll().stream()
            .filter(sub -> sub.isActive() && sub.getEndDate().equals(tomorrow))
            .toList();

    for (Subscription sub : expiringSoon) {
        String email = sub.getUser().getEmail();
        String message = "Hi " + sub.getUser().getName() + ",\n\nYour ₹1/week subscription will expire tomorrow. Please renew to continue learning!";

        emailService.sendEmail(email, "ShikshaSetu: Subscription Expiry Reminder", message);
    }
    }

    public Subscription createNewSubscription(User user, Course course) {
    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusDays(7);

    Subscription subscription = Subscription.builder()
            .user(user)
            .course(course)
            .startDate(today)
            .endDate(endDate)
            .active(true)
            .build();

    return subscriptionRepository.save(subscription);
    }
}
