package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.StudentDashboardDTO;
import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final EnrollmentRepository enrollmentRepo;
    private final VideoContentRepository videoContentRepo;
    private final VideoWatchHistoryRepository watchRepo;
    private final CertificateRepository certificateRepo;
    private final UserRepository userRepo;

    public List<StudentDashboardDTO> getDashboardData(Principal principal) {
        User student = userRepo.findByEmail(principal.getName()).orElseThrow();

        List<Enrollment> enrollments = enrollmentRepo.findByStudent(student);
        List<StudentDashboardDTO> dashboard = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            Long courseId = course.getId();

            int totalVideos = (int) videoContentRepo.countByCourseId(courseId);
            int videosWatched = (int) watchRepo.countByUserIdAndVideo_Course_Id(student.getId(), courseId);
            boolean hasCertificate = certificateRepo.findByUserIdAndCourseId(student.getId(), courseId).isPresent();

            double progress = totalVideos == 0 ? 0.0 : (videosWatched * 100.0 / totalVideos);

            dashboard.add(new StudentDashboardDTO(
                    courseId,
                    course.getTitle(),
                    totalVideos,
                    videosWatched,
                    progress,
                    hasCertificate
            ));
        }

        return dashboard;
    }
}
