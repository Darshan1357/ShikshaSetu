package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoCommentService {

    @Autowired
    private VideoCommentRepository commentRepo;

    @Autowired
    private VideoContentRepository videoRepo;

    @Autowired
    private UserRepository userRepo;

    public VideoComment addComment(Long videoId, String content, Principal principal) {
        VideoContent video = videoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        VideoComment comment = VideoComment.builder()
                .content(content)
                .video(video)
                .user(user)
                .commentedAt(LocalDateTime.now())
                .build();

        return commentRepo.save(comment);
    }

    public List<VideoComment> getCommentsForVideo(Long videoId) {
        return commentRepo.findByVideoIdOrderByCommentedAtAsc(videoId);
    }
}
