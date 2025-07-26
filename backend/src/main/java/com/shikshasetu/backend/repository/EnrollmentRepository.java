package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.Enrollment;
import com.shikshasetu.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);

    long countByCourseId(Long courseId);
    List<Enrollment> findByCourseInstructorId(Long instructorId);
    long countByCourse(Course course);
}

