package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.AdminVideoStatsDTO;
import com.shikshasetu.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final VideoReactionRepository videoReactionRepo;
    private final VideoCommentRepository videoCommentRepo;
    private final VideoWatchHistoryRepository videoWatchHistoryRepo;
    private final CourseRepository courseRepo;

    public AdminVideoStatsDTO getPlatformVideoStats() {
        long totalViews = videoWatchHistoryRepo.countTotalViews();
        long totalLikes = videoReactionRepo.countTotalLikes();
        long totalComments = videoCommentRepo.countTotalComments();
        long totalCourses = courseRepo.count();

        double avgViewsPerCourse = totalCourses > 0 ? (double) totalViews / totalCourses : 0.0;

        return new AdminVideoStatsDTO(
                totalViews,
                totalLikes,
                totalComments,
                avgViewsPerCourse
        );
    }
    public String getAnalytics() {
        return "Some analytics data";
    }
}
