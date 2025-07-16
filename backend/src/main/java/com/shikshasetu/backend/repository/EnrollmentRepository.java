package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
