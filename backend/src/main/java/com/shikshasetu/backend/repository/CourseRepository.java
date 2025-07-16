package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
