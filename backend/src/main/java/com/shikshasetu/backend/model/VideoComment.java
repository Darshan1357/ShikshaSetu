package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder.Default
    private LocalDateTime commentedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoContent video;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // commenter
}
