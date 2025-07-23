package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Certificate;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepo;  

    @PostMapping("/{courseId}/generate")
    public ResponseEntity<?> generateCertificate(@PathVariable Long courseId, Principal principal) {
    try {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Certificate certificate = certificateService.generateCertificate(user, courseId);
        return ResponseEntity.ok(certificate); // ✅ returning a result
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage()); // ✅ handling error case
    }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCertificate(@PathVariable Long courseId, Principal principal) {
    try {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Certificate certificate = certificateService.getCertificate(user, courseId);
        return ResponseEntity.ok(certificate); // ✅ result returned
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }
}
