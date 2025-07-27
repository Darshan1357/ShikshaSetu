package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.VideoComment;
import com.shikshasetu.backend.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
public class VideoCommentController {

    @Autowired
    private VideoCommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long videoId,
                                        @RequestBody String content,
                                        Principal principal) {
        VideoComment comment = commentService.addComment(videoId, content, principal);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<VideoComment>> getComments(@PathVariable Long videoId) {
        return ResponseEntity.ok(commentService.getCommentsForVideo(videoId));
    }
}
