package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.CourseService;
//import com.shikshasetu.backend.util.JwtUtil;
import org.springframework.data.domain.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Course APIs", description = "Endpoints for managing courses")
@RestController
@RequestMapping("/api/courses/search-advanced")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get course by ID", description = "Returns course details for the given ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Course fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    // ✅ GET All Courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // ✅ GET Course by ID
    @Operation(summary = "Get course by ID", description = "Returns course details for the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
    Course course = courseService.getCourseById(id).orElseThrow(() -> new RuntimeException("Course not found"));

    return ResponseEntity.ok(course);
    }

    @Operation(summary = "Add a new course", description = "Creates a new course for an instructor")
    @ApiResponse(responseCode = "201", description = "Course created successfully")
    // ✅ Add Course (Instructor only)
    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody Course course, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        if (user.getRole() == Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can add courses");
        }
        course.setInstructor(user);
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    @Operation(summary = "Search Courses")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful"),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "404", description = "Course not found")
    })
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

    // ✅ UPDATE Course (Instructor only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    if (user.getRole() == Role.INSTRUCTOR) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can update courses");
    }

    Course updated = courseService.updateCourse(id, updatedCourse, user);
    return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete course by ID", description = "Deletes the course with the specified ID")
    @ApiResponse(responseCode = "200", description = "Course deleted successfully")
    // ✅ DELETE Course (Instructor only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();

    if (user.getRole() == Role.INSTRUCTOR) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructors can delete courses");
    }

    courseService.deleteCourse(id, user);
    return ResponseEntity.ok("Course deleted successfully");
    }
}
