package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.VideoWatchHistory;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.model.VideoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoWatchHistoryRepository extends JpaRepository<VideoWatchHistory, Long> {
    List<VideoWatchHistory> findByUser(User user);
    List<VideoWatchHistory> findByVideo(VideoContent video);
    List<VideoWatchHistory> findByUserId(Long userId);
    List<VideoWatchHistory> findByVideoId(Long videoId);
    List<VideoWatchHistory> findByUserIdAndVideoId(Long userId, Long videoId);
    
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);   
    Long countByVideo_Id(Long videoId); 
    long countByUserIdAndVideo_Course_Id(Long userId, Long courseId);
    long countByVideoCourseId(Long courseId);
    @Query("SELECT COUNT(vw) FROM VideoWatchHistory vw")
    long countTotalViews();
}

