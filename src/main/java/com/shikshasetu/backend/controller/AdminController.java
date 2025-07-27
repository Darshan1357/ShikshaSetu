package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.AdminDashboardStatsDTO;
//import com.shikshasetu.backend.dto.AdminVideoStatsDTO;
import lombok.RequiredArgsConstructor;
import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.CertificateRepository;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.EnrollmentRepository;
import com.shikshasetu.backend.repository.SubscriptionRepository;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.repository.VideoContentRepository;
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
    private final VideoContentRepository videoContentRepo;
    private final CertificateRepository certificateRepo;
    private final SubscriptionRepository subscriptionRepo;

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
        user.setRole(Role.INSTRUCTOR);
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
    public ResponseEntity<AdminDashboardStatsDTO> getAdminDashboardStats() {
        long totalUsers = userRepo.count();
        long totalStudents = userRepo.countByRole(Role.STUDENT);
        long totalInstructors = userRepo.countByRole(Role.INSTRUCTOR);
        long totalCourses = courseRepo.count();
        long totalEnrollments = enrollmentRepo.count();
        long totalVideos = videoContentRepo.count();
        long totalCertificates = certificateRepo.count();
        long activeSubscriptions = subscriptionRepo.countByActiveTrue();

        double totalrevenue = totalEnrollments * 1.0; // ₹1 per enrollment

        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO(
                totalUsers,
                totalStudents,
                totalInstructors,
                totalCourses,
                totalVideos,
                totalEnrollments,
                totalCertificates,
                activeSubscriptions,
                totalrevenue
        );

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
