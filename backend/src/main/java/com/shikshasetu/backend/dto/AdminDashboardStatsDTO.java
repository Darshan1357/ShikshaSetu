package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardStatsDTO{
    private long totalUsers;
    private long totalStudents;
    private long totalInstructors;
    private long totalAdmins;
    private long totalCourses;
    private long totalEnrollments;
    private double totalRevenue;
}
