package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.InstructorCourseEarningsDTO;
import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorStatsService {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    public List<InstructorCourseEarningsDTO> getInstructorStats(User instructor) {
        List<Course> courses = courseRepo.findByInstructor(instructor);

        return courses.stream()
                .map(course -> {
                    long enrollments = enrollmentRepo.countByCourseId(course.getId());
                    double revenue = enrollments * 1.0; // ₹1 per enrollment
                    return new InstructorCourseEarningsDTO(
                            course.getId(),
                            course.getTitle(),
                            enrollments,
                            revenue
                    );
                })
                .collect(Collectors.toList());
    }
}
