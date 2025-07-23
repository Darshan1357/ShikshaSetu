package com.shikshasetu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstructorCourseEarningsDTO {
    private Long courseId;
    private String courseTitle;
    private long enrollmentCount;
    private double revenueGenerated;
}
