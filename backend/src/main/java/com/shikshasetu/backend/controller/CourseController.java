package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.CourseService;
//import com.shikshasetu.backend.util.JwtUtil;
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
}
