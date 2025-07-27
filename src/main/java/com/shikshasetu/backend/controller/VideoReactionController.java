package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.VideoContent;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.service.VideoContentService;
import com.shikshasetu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/videos/reactions")
public class VideoReactionController {

    @Autowired
    private VideoContentService videoService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{videoId}/like")
    public ResponseEntity<?> likeVideo(@PathVariable Long videoId, Principal principal) {
       User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        videoService.reactToVideo(user.getId(), videoId, true);

        return ResponseEntity.ok("Liked the video.");
    }

    @PostMapping("/{videoId}/dislike")
    public ResponseEntity<?> dislikeVideo(@PathVariable Long videoId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        videoService.reactToVideo(user.getId(), videoId, false);
        
        return ResponseEntity.ok("Disliked the video.");
    }

    @GetMapping("/{videoId}/stats")
    public ResponseEntity<?> getReactionStats(@PathVariable Long videoId) {
        long likes = videoService.getLikesCount(videoId);
        long dislikes = videoService.getDislikesCount(videoId);
        return ResponseEntity.ok("👍 " + likes + " | 👎 " + dislikes);
    }

    @GetMapping("/user/watched")
    public ResponseEntity<?> getWatchedVideos(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<VideoContent> watched = videoService.getWatchedVideosByUserId(user.getId());
        return ResponseEntity.ok(watched);
    }
}
