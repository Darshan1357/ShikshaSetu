package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;

@Data
@AllArgsConstructor
public class VideoWatchInfoDTO {
    private Long userId;
    private String userName;

    @CreationTimestamp 
    @Column(name = "watched_at")
    private LocalDateTime watchedAt;

    private boolean completed;

    public LocalDateTime getWatchedAt() {
    return watchedAt;
    }
}
