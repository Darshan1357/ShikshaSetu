package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.dto.InstructorCourseEarningsDTO;
import com.shikshasetu.backend.dto.InstructorDashboardDTO;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.InstructorDashboardService;
import com.shikshasetu.backend.service.InstructorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final UserRepository userRepo;
    private final InstructorStatsService statsService;
    private final InstructorDashboardService dashboardService;

    @GetMapping("/earnings")
    public ResponseEntity<List<InstructorCourseEarningsDTO>> getEarnings(Principal principal) {
        User instructor = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<InstructorCourseEarningsDTO> stats = statsService.getInstructorStats(instructor);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<List<InstructorDashboardDTO>> getDashboard(Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElseThrow();
        List<InstructorDashboardDTO> dashboard = dashboardService.getDashboard(user);
        return ResponseEntity.ok(dashboard);
    }
}
