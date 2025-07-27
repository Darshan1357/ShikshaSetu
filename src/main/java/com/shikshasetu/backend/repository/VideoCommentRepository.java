package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.VideoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
    List<VideoComment> findByVideoIdOrderByCommentedAtAsc(Long videoId);
    Long countByVideo_Id(Long videoId);
    long countByVideoCourseId(Long courseId);
    @Query("SELECT COUNT(vc) FROM VideoComment vc")
    long countTotalComments();
}
