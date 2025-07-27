package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.Subscription;
import com.shikshasetu.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserOrderByEndDateDesc(User user);
    boolean existsByUserAndActiveTrue(User user);
    boolean existsByUserAndCourseAndActiveTrue(User user, Course course);
    List<Subscription> findByActiveFalseAndEndDateBefore(java.time.LocalDate date);
    long countByActiveTrue();
    long countByCourseAndActiveTrue(Course course);
}
