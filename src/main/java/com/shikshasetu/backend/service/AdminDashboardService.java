package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.AdminDashboardStatsDTO;
import com.shikshasetu.backend.model.Role;
import com.shikshasetu.backend.repository.*;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Builder
public class AdminDashboardService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final VideoContentRepository videoContentRepo;
    private final CertificateRepository certificateRepo;
    private final SubscriptionRepository subscriptionRepo;

    public AdminDashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepo.count();
        long totalStudents = userRepo.countByRole(Role.STUDENT);
        long totalInstructors = userRepo.countByRole(Role.INSTRUCTOR);
        long totalCourses = courseRepo.count();
        long totalVideos = videoContentRepo.count();
        long totalEnrollments = enrollmentRepo.count();
        long totalCertificates = certificateRepo.count();
        long activeSubscriptions = subscriptionRepo.countByActiveTrue();

        // Assuming ₹1/week per enrollment
        double totalRevenue = totalEnrollments * 1.0;

        return AdminDashboardStatsDTO.builder()
            .totalUsers(totalUsers)
            .totalStudents(totalStudents)
            .totalInstructors(totalInstructors)
            .totalCourses(totalCourses)
            .totalVideos(totalVideos)
            .totalEnrollments(totalEnrollments)
            .totalCertificates(totalCertificates)
            .activeSubscriptions(activeSubscriptions)
            .totalRevenue(totalRevenue)
            .build();
    }
}

