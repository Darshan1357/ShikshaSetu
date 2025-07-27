package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstructorDashboardDTO {
    private Long courseId;
    private String title;
    private String createdDate;
    private long enrollments;
    private long videoCount;
    private long totalViews;
    private long totalLikes;
    private long totalComments;
}
