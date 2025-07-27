package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/search")
public class CourseSearchController {

    @Autowired
    private CourseSearchService courseSearchService;

    @GetMapping
    public ResponseEntity<?> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Course> results = courseSearchService.searchCourses(keyword, category, sortBy, page, size);
        return ResponseEntity.ok(results);
    }
}
