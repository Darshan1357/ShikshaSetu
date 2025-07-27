package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.StudentDashboardDTO;
import com.shikshasetu.backend.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<List<StudentDashboardDTO>> getDashboard(Principal principal) {
        List<StudentDashboardDTO> dashboard = dashboardService.getDashboardData(principal);
        return ResponseEntity.ok(dashboard);
    }
}
