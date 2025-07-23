package com.shikshasetu.backend.dto;

public class VideoAnalyticsDTO {
    private Long videoId;
    private Long views;
    private Long likes;
    private Long dislikes;
    private Long comments;

    // Constructors
    public VideoAnalyticsDTO() {}

    public VideoAnalyticsDTO(Long videoId, Long views, Long likes, Long dislikes, Long comments) {
        this.videoId = videoId;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
    }

    // Getters & Setters
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }
}
