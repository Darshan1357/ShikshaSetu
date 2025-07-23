package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.CourseService;
//import com.shikshasetu.backend.util.JwtUtil;
import org.springframework.data.domain.Page;
//import com.shikshasetu.backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    // ✅ GET All Courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // ✅ GET Course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
    Course course = courseService.getCourseById(id).orElseThrow(() -> new RuntimeException("Course not found"));

    return ResponseEntity.ok(course);
    }

    // ✅ UPDATE Course (Instructor only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    if (!user.getRole().equalsIgnoreCase("INSTRUCTOR")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can update courses");
    }

    Course updated = courseService.updateCourse(id, updatedCourse, user);
    return ResponseEntity.ok(updated);
    }

    // ✅ DELETE Course (Instructor only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    if (!user.getRole().equalsIgnoreCase("INSTRUCTOR")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can delete courses");
    }

    courseService.deleteCourse(id, user);
    return ResponseEntity.ok("Course deleted successfully");
    }

    // ✅ Add Course (Instructor only)
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody Course course, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (!user.getRole().equalsIgnoreCase("INSTRUCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can add courses");
        }
        course.setInstructor(user);
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Course>> searchCourses(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String instructor,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdDate") String sortBy
    ) {
    Page<Course> results = courseService.searchCourses(
            keyword, category, instructor, minPrice, maxPrice, page, size, sortBy
    );
    return ResponseEntity.ok(results);
    }
}
