package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean liked; // true for like, false for dislike

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoContent video;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reaction")
    private String reaction;

    private String type;
}
