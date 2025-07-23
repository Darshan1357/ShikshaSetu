package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminVideoStatsDTO {
    private long totalViews;
    private long totalLikes;
    private long totalComments;
    private double avgViewsPerCourse;
}
