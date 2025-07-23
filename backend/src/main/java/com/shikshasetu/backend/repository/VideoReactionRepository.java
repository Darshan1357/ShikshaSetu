package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.VideoReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VideoReactionRepository extends JpaRepository<VideoReaction, Long> {
    Optional<VideoReaction> findByUserIdAndVideoId(Long userId, Long videoId);
    long countByVideoIdAndLikedTrue(Long videoId); // Like count
    long countByVideoIdAndLikedFalse(Long videoId); // Dislike count
    Long countByVideo_IdAndReaction(Long videoId, String reaction);
    Long countByVideo_IdAndType(Long videoId, String type);
    long countByVideoCourseIdAndReaction(Long courseId, String reaction);
    long countByReaction(String reaction);
    @Query("SELECT COUNT(vr) FROM VideoReaction vr WHERE vr.reaction = 'like'")
    long countTotalLikes();
}
