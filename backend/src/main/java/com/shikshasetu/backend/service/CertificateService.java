package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
//import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private VideoContentRepository videoContentRepo;

    @Autowired
    private VideoWatchHistoryRepository watchHistoryRepo;

    @Autowired
    private CertificateRepository certificateRepo;

    public Certificate generateCertificate(User user, Long courseId) {
        // 🔐 Step 1: Confirm user is enrolled
        Course course = enrollmentRepo.findByStudentIdAndCourseId(user.getId(), courseId)
                .map(Enrollment::getCourse)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course."));

        // 🧮 Step 2: Get total video count for course
        long totalVideos = videoContentRepo.countByCourseId(courseId);

        // 👀 Step 3: Get how many videos the user has watched
        long watchedCount = watchHistoryRepo.countByUserIdAndVideo_Course_Id(user.getId(), courseId);

        // ✅ Step 4: Check if all videos watched
        if (watchedCount < totalVideos) {
            throw new RuntimeException("Please complete all videos before generating certificate.");
        }

        // 🧾 Step 5: Check if certificate already issued
        Optional<Certificate> existing = certificateRepo.findByUserIdAndCourseId(user.getId(), courseId);
        if (existing.isPresent()) return existing.get();

        // 🪪 Step 6: Generate new certificate
        Certificate cert = new Certificate();
        cert.setUser(user);
        cert.setCourse(course);
        cert.setIssuedDate(LocalDate.now());
        cert.setCertificateCode("CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return certificateRepo.save(cert);
    }

    public Certificate getCertificate(User user, Long courseId) {
    return certificateRepo.findByUserIdAndCourseId(user.getId(), courseId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
}
