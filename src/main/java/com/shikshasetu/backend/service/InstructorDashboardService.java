package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.dto.InstructorDashboardDTO;
import com.shikshasetu.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InstructorDashboardService {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final VideoContentRepository videoContentRepo;
    private final VideoReactionRepository reactionRepo;
    private final VideoCommentRepository commentRepo;
    private final VideoWatchHistoryRepository watchHistoryRepo;

    public List<InstructorDashboardDTO> getDashboard(User instructor) {
        List<Course> courses = courseRepo.findByInstructor(instructor);
        List<InstructorDashboardDTO> statsList = new ArrayList<>();

        for (Course course : courses) {
            Long courseId = course.getId();

            long enrollments = enrollmentRepo.countByCourseId(courseId);
            long  videoCount = videoContentRepo.countByCourseId(courseId);
            long views = watchHistoryRepo.countByVideoCourseId(courseId);
            long likes = reactionRepo.countByVideoCourseIdAndReaction(courseId, "LIKE");
            long comments = commentRepo.countByVideoCourseId(courseId);

            statsList.add(new InstructorDashboardDTO(
                courseId,
                course.getTitle(),
                formatDate(course.getCreatedDate()),
                enrollments,
                videoCount,
                views,
                likes,
                comments
            ));
        }

        return statsList;
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        return dateTime.format(formatter);
    }
}
