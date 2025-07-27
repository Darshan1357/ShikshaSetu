package com.shikshasetu.backend.service;

import com.shikshasetu.backend.dto.VideoAnalyticsDTO;
import com.shikshasetu.backend.exception.ResourceNotFoundException;
import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.model.Enrollment;
import com.shikshasetu.backend.model.VideoContent;
import com.shikshasetu.backend.model.VideoReaction;
import com.shikshasetu.backend.model.VideoWatchHistory;
import com.shikshasetu.backend.repository.CourseRepository;
import com.shikshasetu.backend.repository.EnrollmentRepository;
import com.shikshasetu.backend.repository.VideoContentRepository;
import com.shikshasetu.backend.repository.VideoReactionRepository;
import com.shikshasetu.backend.repository.VideoWatchHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.repository.VideoCommentRepository;

import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class VideoContentService {

    @Autowired
    private VideoContentRepository videoContentRepo;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VideoWatchHistoryRepository watchHistoryRepo;
    
    @Autowired
    private VideoReactionRepository videoReactionRepo;

    @Autowired
    private VideoWatchHistoryRepository videoWatchHistoryRepo;

    @Autowired
    private VideoCommentRepository videoCommentRepo;

    public VideoContent uploadVideo(Long courseId, VideoContent videoContent) {
    Optional<Course> courseOptional = courseRepository.findById(courseId);
    if (courseOptional.isPresent()) {
        videoContent.setCourse(courseOptional.get());
        return videoContentRepo.save(videoContent);
    }
    throw new RuntimeException("Course not found");
    }

    public List<VideoContent> getAllVideosByCourseId(Long courseId) {
        return videoContentRepo.findByCourseId(courseId);
    }
     public Optional<VideoContent> getVideoById(Long id) {
        return videoContentRepo.findById(id);
    }
    public Course getCourseById(Long courseId) {
    return courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
    return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }


    // Update video by instructor only
    public VideoContent updateVideo(Long videoId, VideoContent updatedVideo, Principal principal) {
    VideoContent video = videoContentRepo.findById(videoId)
        .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

    User user = userRepo.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Only the instructor who owns the course can update the video
    if (!video.getCourse().getInstructor().getId().equals(user.getId())) {
        throw new AccessDeniedException("Only the instructor can update this video.");
    }

    // Update fields
    video.setTitle(updatedVideo.getTitle());
    video.setVideoUrl(updatedVideo.getVideoUrl());
    video.setSequenceOrder(updatedVideo.getSequenceOrder());
    video.setFreePreview(updatedVideo.isFreePreview());
    video.setDescription(updatedVideo.getDescription());

    return videoContentRepo.save(video);
    }


    // Delete video by instructor only
    public ResponseEntity<?> deleteVideo(Long videoId, Principal principal) {
    VideoContent video = videoContentRepo.findById(videoId)
        .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

    User user = userRepo.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Instructor-only access
    if (!video.getCourse().getInstructor().getId().equals(user.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this video.");
    }

    videoContentRepo.delete(video);
    return ResponseEntity.ok("Video deleted successfully.");
    }

    public boolean isAccessValid(Long userId, Long courseId) {
    Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

    LocalDateTime enrolledOn = enrollment.getEnrolledOn();
    return enrolledOn.plusDays(7).isAfter(LocalDateTime.now());
    }

    public ResponseEntity<?> getVideoIfAllowed(Long videoId, Principal principal) {
    VideoContent video = videoContentRepo.findById(videoId)
            .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

    Course course = video.getCourse();

    // If it's a free preview, allow access to all
    if (video.isFreePreview()) {
        return ResponseEntity.ok(video);
    }

    // Get current user
    User user = userRepo.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    boolean isInstructor = course.getInstructor().getId().equals(user.getId());
    boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), course.getId());
    boolean hasAccess = isAccessValid(user.getId(), course.getId());

    if (isInstructor || (isEnrolled && hasAccess)) {
        return ResponseEntity.ok(video);
    }
    throw new AccessDeniedException("You don't have access to view this video.");
    }

    public void trackVideoWatch(User user, VideoContent video) {
    VideoWatchHistory history = VideoWatchHistory.builder()
        .user(user)
        .video(video)
        .watchedAt(LocalDateTime.now())
        .build();
    watchHistoryRepo.save(history);
    }
    
    public void reactToVideo(Long userId, Long videoId, boolean like) {
    Optional<VideoReaction> existing = videoReactionRepo.findByUserIdAndVideoId(userId, videoId);
    if (existing.isPresent()) {
        // update existing reaction
        VideoReaction reaction = existing.get();
        reaction.setLiked(like);
        videoReactionRepo.save(reaction);
    } else {
        // new reaction
        VideoReaction reaction = VideoReaction.builder()
                .user(userRepo.findById(userId).orElseThrow())
                .video(videoContentRepo.findById(videoId).orElseThrow())
                .liked(like)
                .build();
        videoReactionRepo.save(reaction);
    }
    }

    public String generateSecureVideoUrl(String originalUrl) {
    long expiryTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes
    String secureToken = generateHash(originalUrl + expiryTime);    
    return originalUrl + "?expiry=" + expiryTime + "&token=" + secureToken;
    }

    private String generateHash(String input) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error generating hash", e);
    }
    }

    public long getLikesCount(Long videoId) {
    return videoReactionRepo.countByVideoIdAndLikedTrue(videoId);
    }

    public long getDislikesCount(Long videoId) {
    return videoReactionRepo.countByVideoIdAndLikedFalse(videoId);
    }

    public List<VideoContent> getWatchedVideosByUserId(Long userId) {
    List<VideoWatchHistory> history = watchHistoryRepo.findByUserId(userId);
    return history.stream()
            .map(VideoWatchHistory::getVideo)
            .distinct()
            .collect(Collectors.toList());
    }

    public void markVideoComplete(Long userId, Long videoId) {
   List<VideoWatchHistory> historyList = videoWatchHistoryRepo.findByUserId(userId);
    if (historyList.isEmpty()) {
        throw new RuntimeException("No watch history found");
    }
    }
    
    public double getCourseProgress(Long userId, Long courseId) {
    List<VideoContent> courseVideos = videoContentRepo.findByCourseId(courseId);
    if (courseVideos.isEmpty()) return 0.0;

    long completedCount = videoWatchHistoryRepo
            .findByUserId(userId).stream()
            .filter(v -> v.isCompleted() && v.getVideo().getCourse().getId().equals(courseId))
            .count();

    return (completedCount * 100.0) / courseVideos.size();
    }

    public VideoAnalyticsDTO getVideoAnalytics(Long videoId) {
    Long views = videoWatchHistoryRepo.countByVideo_Id(videoId);
    Long likes = videoReactionRepo.countByVideo_IdAndReaction(videoId, "LIKE");
    Long dislikes = videoReactionRepo.countByVideo_IdAndReaction(videoId, "DISLIKE");
    Long comments = videoCommentRepo.countByVideo_Id(videoId);

    return new VideoAnalyticsDTO(videoId, views, likes, dislikes, comments);
    }
}