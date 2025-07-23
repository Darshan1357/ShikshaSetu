package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.VideoAnalyticsDTO;
import com.shikshasetu.backend.dto.VideoWatchInfoDTO;
import com.shikshasetu.backend.exception.ResourceNotFoundException;
import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.model.VideoContent;
import com.shikshasetu.backend.model.VideoWatchHistory;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.repository.VideoContentRepository;
import com.shikshasetu.backend.repository.VideoWatchHistoryRepository;
import com.shikshasetu.backend.service.SubscriptionService;
import com.shikshasetu.backend.service.VideoContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoContentController {

    @Autowired
    private VideoContentService videoService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VideoContentRepository videoContentRepo;

    @Autowired
    private VideoWatchHistoryRepository videoWatchHistoryRepo;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private SubscriptionService subscriptionService;


    @PostMapping("/upload/{courseId}")
    public ResponseEntity<?> uploadVideo(@PathVariable Long courseId,
                                         @RequestBody VideoContent videoContent,
                                         Principal principal) {

        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user == null || !user.getRole().toString().equals("INSTRUCTOR")) {
            return ResponseEntity.status(403).body("Only instructors can upload videos.");
        }

        VideoContent savedVideo = videoService.uploadVideo(courseId, videoContent);
        return ResponseEntity.ok(savedVideo);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getVideosByCourse(@PathVariable Long courseId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Course course = videoService.getCourseById(courseId);

    boolean isInstructor = course.getInstructor().getId().equals(user.getId());
    boolean isEnrolled = videoService.isUserEnrolledInCourse(user.getId(), courseId);

    if (isInstructor || isEnrolled) {
        List<VideoContent> videos = videoService.getAllVideosByCourseId(courseId);
        return ResponseEntity.ok(videos);
    } else {
        return ResponseEntity.status(403).body("You are not enrolled in this course.");
    }
    }   
    
    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideoById(@PathVariable Long videoId, Principal principal) {
    Optional<VideoContent> videoOpt = videoService.getVideoById(videoId);
    if (videoOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
    }
    
    VideoContent video = videoOpt.get();
    video.setVideoUrl(videoService.generateSecureVideoUrl(video.getVideoUrl()));
    Course course = video.getCourse();  // Get the course for this video
    
    // Allow access if it's a free preview
    if (video.isFreePreview()) {
        return ResponseEntity.ok(video);
    }

    // If principal is null (not logged in), deny access
    if (principal == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login required to access this video.");
    }

    // Get the currently logged-in user
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Check if user is instructor or enrolled with valid access
    boolean isInstructor = course.getInstructor().getId().equals(user.getId());
    boolean isEnrolled = videoService.isUserEnrolledInCourse(user.getId(), course.getId());
    boolean isAccessValid = videoService.isAccessValid(user.getId(), course.getId());

    if (isInstructor || (isEnrolled && isAccessValid)) {
        videoService.trackVideoWatch(user, video);
        return ResponseEntity.ok("Watch recorded!"+video);
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your access has expired or you're not enrolled.");
    }
    }

    @PutMapping("/update/{videoId}")
    public ResponseEntity<?> updateVideo(@PathVariable Long videoId,
                                     @RequestBody VideoContent updatedVideo,
                                     Principal principal) {
    try {
        VideoContent result = videoService.updateVideo(videoId, updatedVideo, principal);
        return ResponseEntity.ok(result);
    } catch (AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long videoId, Principal principal) {
    try {
        videoService.deleteVideo(videoId, principal);
        return ResponseEntity.ok("Video deleted successfully.");
    } catch (AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }

    @GetMapping("/course/{courseId}/page")
    public ResponseEntity<?> getPagedVideos(
        @PathVariable Long courseId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by("sequenceOrder"));
    Page<VideoContent> pagedVideos = videoContentRepo.findByCourseId(courseId, pageable);

    return ResponseEntity.ok(pagedVideos);
    }

    @GetMapping("/course/{courseId}/progress")
    public ResponseEntity<?> getCourseProgress(@PathVariable Long courseId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    double progress = videoService.getCourseProgress(user.getId(), courseId);
    Map<String, Object> response = new HashMap<>();
    response.put("progress", progress);
    return ResponseEntity.ok(response);
    }

    @PutMapping("/{videoId}/complete")
    public ResponseEntity<?> markVideoComplete(@PathVariable Long videoId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    videoService.markVideoComplete(user.getId(), videoId);
    return ResponseEntity.ok("Video marked as complete.");
    }

    @GetMapping("/course/{courseId}/completed")
    public ResponseEntity<?> getCompletedVideos(
        @PathVariable Long courseId,
        Principal principal) {
    
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<VideoWatchHistory> completed = videoWatchHistoryRepo.findByUserId(user.getId()).stream()
            .filter(v -> v.isCompleted() && v.getVideo().getCourse().getId().equals(courseId))
            .toList();

    return ResponseEntity.ok(completed);
    }
    
    @GetMapping("/analytics/{videoId}")
    public ResponseEntity<VideoAnalyticsDTO> getAnalytics(@PathVariable Long videoId) {
    return ResponseEntity.ok(videoContentService.getVideoAnalytics(videoId));
    }

    @GetMapping("/secure-access/{courseId}")
    public ResponseEntity<?> accessVideo(@PathVariable Long courseId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    if (!subscriptionService.hasActiveSubscription(user, course)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Your subscription has expired.");
    }

    return ResponseEntity.ok("You have active access to this course.");
    }

    @GetMapping("/{videoId}/watch-history")
    public ResponseEntity<?> getWatchHistory(@PathVariable Long videoId, Principal principal) {
    User requester = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    VideoContent video = videoContentRepo.findById(videoId)
            .orElseThrow(() -> new RuntimeException("Video not found"));

    Course course = video.getCourse();
    if (!requester.getId().equals(course.getInstructor().getId()) &&
        !requester.getRole().toString().equals("ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only instructor or admin can view watch history.");
    }

    List<VideoWatchHistory> historyList = videoWatchHistoryRepo.findByVideoId(videoId);

    List<VideoWatchInfoDTO> response = historyList.stream()
            .map(v -> new VideoWatchInfoDTO(
                    v.getUser().getId(),
                    v.getUser().getEmail(),  // Or name, if preferred
                    v.getWatchedAt(),
                    v.isCompleted()
            ))
            .toList();

    return ResponseEntity.ok(response);
    }

    @GetMapping("/secure-link/{videoId}")
    public ResponseEntity<?> getSecureLink(@PathVariable Long videoId, Principal principal) {
    Optional<VideoContent> videoOpt = videoContentService.getVideoById(videoId);
    if (videoOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
    }
    VideoContent video = videoOpt.get();
    String secureLink = videoContentService.generateSecureVideoUrl(video.getVideoUrl());
    Map<String, Object> response = new HashMap<>();
    response.put("secureUrl", secureLink);
    response.put("expiresIn", "5 minutes");
    return ResponseEntity.ok(response);
    }
}

