package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

    @Service
    public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;  

    @Autowired
    private CourseRepository courseRepo;

    public Enrollment enroll(User student, Course course) {
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Already enrolled");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledOn(LocalDateTime.now())
                .accessExpiresOn(LocalDateTime.now().plusWeeks(1)) // ⏱️ access 1 week
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByStudent(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getEnrollmentsByCourse(Course course) {
        return enrollmentRepository.findByCourse(course);
    }
    public List<Course> getCoursesForStudent(String email) {
    User student = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

    return enrollments.stream()
            .map(Enrollment::getCourse)
            .collect(Collectors.toList());
    }
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
    return enrollmentRepository.findByCourseId(courseId);
    }
    
    public List<Course> getEnrolledCoursesByStudentEmail(String email) {
    User student = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

    return enrollments.stream()
            .map(Enrollment::getCourse)
            .collect(Collectors.toList());
    }

    public Enrollment enrollStudent(Long courseId, User student) {
    Course course = courseRepo.findById(courseId)
        .orElseThrow(() -> new RuntimeException("Course not found"));

    if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
        throw new RuntimeException("Already enrolled in this course.");
    }

    Enrollment enrollment = new Enrollment();
    enrollment.setStudent(student);
    enrollment.setCourse(course);
    enrollment.setEnrolledOn(LocalDateTime.now());
    Enrollment saved = enrollmentRepository.save(enrollment);

    // 🔔 Email notification
    // ✅ Send course enrollment confirmation
    emailService.sendEmail(
    student.getEmail(),
    "📚 Enrolled in " + course.getTitle(),
    "Hello " + student.getName() + ",\n\n" +
    "You’ve successfully enrolled in: " + course.getTitle() + "\n" +
    "Start learning now and complete the videos to earn your certificate!\n\n" +
    "Regards,\nShikshaSetu Team"
    );
    return saved;
    }
}
