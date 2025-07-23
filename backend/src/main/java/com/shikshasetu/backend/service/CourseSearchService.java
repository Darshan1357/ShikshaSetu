package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CourseSearchService {

    @Autowired
    private CourseRepository courseRepo;

    public Page<Course> searchCourses(String keyword, String category, String sortBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        if (keyword != null && category != null) {
            return courseRepo.findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(keyword, category, pageable);
        } else if (keyword != null) {
            return courseRepo.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if (category != null) {
            return courseRepo.findByCategoryIgnoreCase(category, pageable);
        } else {
            return courseRepo.findAll(pageable);
        }
    }
}
