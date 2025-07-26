package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.EnrollmentRepository;
import com.shikshasetu.backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsExportService {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final SubscriptionRepository subscriptionRepo;

    public byte[] generateCSVReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer,CSVFormat.Builder.create(CSVFormat.DEFAULT)
    .setHeader("Course ID", "Course Title", "Instructor", "Enrollments", "Active Subscriptions")
    .build())) {

            List<Course> courses = courseRepo.findAll();
            for (Course course : courses) {
                long enrollments = enrollmentRepo.countByCourse(course);
                long activeSubs = subscriptionRepo.countByCourseAndActiveTrue(course);
                String instructorName = course.getInstructor().getName();

                csvPrinter.printRecord(
                        course.getId(),
                        course.getTitle(),
                        instructorName,
                        enrollments,
                        activeSubs
                );
            }

            csvPrinter.flush();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }
}
