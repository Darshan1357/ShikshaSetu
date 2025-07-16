package com.shikshasetu.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String category;

    private String thumbnailUrl;

    private double price;

    @Column(name = "duration_in_weeks", nullable = false)
    private int durationInWeeks;


    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;  // link to the instructor who created it
}
