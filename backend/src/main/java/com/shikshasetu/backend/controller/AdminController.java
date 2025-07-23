package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.AdminDashboardStatsDTO;
import com.shikshasetu.backend.dto.AdminVideoStatsDTO;

import lombok.RequiredArgsConstructor;
import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.EnrollmentRepository;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.AdminAnalyticsService;
import com.shikshasetu.backend.util.CsvExportUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminAnalyticsService adminAnalyticsService;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepo.findAll());
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
        courseRepo.deleteById(courseId);
        return ResponseEntity.ok("Course deleted successfully.");
    }

    // 🔐 Optional: Promote to Instructor
    @PostMapping("/promote/{userId}")
    public ResponseEntity<String> promoteToInstructor(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("INSTRUCTOR");
        userRepo.save(user);
        return ResponseEntity.ok("User promoted to Instructor.");
    }

    // 🔐 Optional: Block/Unblock user
    @PutMapping("/toggle-block/{userId}")
    public ResponseEntity<String> toggleBlockUser(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepo.save(user);
        return ResponseEntity.ok("User " + (user.isActive() ? "unblocked" : "blocked") + " successfully.");
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        long totalUsers = userRepo.count();
        long totalStudents = userRepo.countByRole("STUDENT");
        long totalInstructors = userRepo.countByRole("INSTRUCTOR");
        long totalAdmins = userRepo.countByRole("ADMIN");

        long totalCourses = courseRepo.count();
        long totalEnrollments = enrollmentRepo.count();

        double revenue = totalEnrollments * 1.0; // ₹1 per enrollment

        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO(
                totalUsers,
                totalStudents,
                totalInstructors,
                totalAdmins,
                totalCourses,
                totalEnrollments,
                revenue
        );

        return ResponseEntity.ok(stats);
    }
    @GetMapping("/admin/video-stats")
    public ResponseEntity<AdminVideoStatsDTO> getVideoStats() {
    AdminVideoStatsDTO stats = adminAnalyticsService.getPlatformVideoStats();
    return ResponseEntity.ok(stats);
    }

    @GetMapping("/analytics")
    public String getAdminAnalytics() {
        return adminAnalyticsService.getAnalytics();
    }

    @GetMapping("/export/courses")
    public ResponseEntity<?> exportCoursesToCSV() {
    List<Course> courses = courseRepo.findAll();
    ByteArrayInputStream csv = CsvExportUtil.coursesToCSV(courses);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=courses.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(new InputStreamResource(csv));
    }
}
