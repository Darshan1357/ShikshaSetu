package com.shikshasetu.backend.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import java.util.Date;
import lombok.*;
//import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String category;

    private String thumbnailUrl;

    private double price;

    @Column(name = "duration_in_weeks", nullable = false)
    private Integer durationInWeeks;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;    

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;  // link to the instructor who created it

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoContent> videos = new ArrayList<>();

    public List<VideoContent> getVideos() {
    return videos;
    }

    public void setVideos(List<VideoContent> videos) {
    this.videos = videos;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
