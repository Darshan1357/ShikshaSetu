package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardStatsDTO{
    private long totalUsers;
    private long totalStudents;
    private long totalInstructors;
    private long totalCourses;
    private long totalVideos;
    private long totalEnrollments;
    private long totalCertificates;
    private long activeSubscriptions;
    private double totalRevenue;
}
