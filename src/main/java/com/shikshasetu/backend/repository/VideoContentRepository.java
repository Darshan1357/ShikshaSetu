package com.shikshasetu.backend.repository;

import com.shikshasetu.backend.model.VideoContent;
//import com.shikshasetu.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface VideoContentRepository extends JpaRepository<VideoContent, Long> {
    List<VideoContent> findByCourseId(Long courseId);
    Page<VideoContent> findByCourseId(Long courseId, Pageable pageable);
    long countByCourseId(Long courseId);
}


