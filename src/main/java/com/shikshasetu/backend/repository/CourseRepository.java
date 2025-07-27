package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
     // Search by title containing keywords (case insensitive)
    Page<Course> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Search by category
    Page<Course> findByCategoryIgnoreCase(String category, Pageable pageable);

    // Combine both if needed later
    Page<Course> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String keyword, String category, Pageable pageable);

    List<Course> findByInstructor(User instructor);
}
