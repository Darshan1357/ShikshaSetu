package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import com.shikshasetu.backend.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // 🧑‍🎓 Student enroll in a course
    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<?> enroll(@PathVariable Long courseId, Principal principal) {
    try {
        System.out.println("➡️ Enroll called for courseId: " + courseId);

        User student = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (student.getRole() == Role.STUDENT) {
            return ResponseEntity.status(403).body("Only students can enroll.");
        }

        Enrollment enrolled = enrollmentService.enroll(student, course);
        return ResponseEntity.ok(enrolled);

        } catch (Exception e) {
            e.printStackTrace(); // This will show in your terminal
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getEnrollmentsByCourse(@PathVariable Long courseId) {
    List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
    if (enrollments.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No enrollments found for course ID: " + courseId);
    }
    return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/my-courses")
    public ResponseEntity<?> getMyEnrollments(Principal principal) {
    String userEmail = principal.getName();
    List<Course> enrolledCourses = enrollmentService.getEnrolledCoursesByStudentEmail(userEmail);

    if (enrolledCourses.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You haven't enrolled in any courses.");
    }
    return ResponseEntity.ok(enrolledCourses);
    }

}
