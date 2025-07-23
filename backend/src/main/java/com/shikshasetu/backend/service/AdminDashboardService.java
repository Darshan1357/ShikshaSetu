package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.AdminDashboardStatsDTO;
import com.shikshasetu.backend.model.Role;
import com.shikshasetu.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    public AdminDashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepo.count();
        long totalStudents = userRepo.countByRole(Role.STUDENT);
        long totalInstructors = userRepo.countByRole(Role.INSTRUCTOR);
        long totalAdmins = userRepo.countByRole(Role.ADMIN);

        long totalCourses = courseRepo.count();
        long totalEnrollments = enrollmentRepo.count();

        // Assuming ₹1/week per enrollment
        double totalRevenue = totalEnrollments * 1.0;

        return new AdminDashboardStatsDTO(
                totalUsers,
                totalStudents,
                totalInstructors,
                totalAdmins,
                totalCourses,
                totalEnrollments,
                totalRevenue
        );
    }
}
