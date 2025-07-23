package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDashboardDTO {
    private Long courseId;
    private String courseTitle;
    private int totalVideos;
    private int videosWatched;
    private double progressPercentage;
    private boolean certificateIssued;
}
