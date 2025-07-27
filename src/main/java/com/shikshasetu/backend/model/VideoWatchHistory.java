package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VideoWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoContent video;

    @CreationTimestamp
    @Builder.Default
    @Column(name = "watched_at", nullable = false, updatable = false)
    private LocalDateTime watchedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;
}
