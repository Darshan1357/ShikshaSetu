package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.model.Certificate;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.repository.UserRepository;
import com.shikshasetu.backend.service.CertificateService;
import com.shikshasetu.backend.service.EmailService;
import com.shikshasetu.backend.util.PdfGeneratorUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepo;  
    
    private final EmailService emailService;

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

    @PostMapping("/resend/{courseId}")
    public ResponseEntity<?> resendCertificate(@PathVariable Long courseId, Principal principal) {
        String email = principal.getName();
        Certificate cert = certificateService.getCertificateForUser(email, courseId);

        if (cert == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No certificate found.");
        }

        // Compose and resend email
        emailService.sendEmail(
                email,
                "📩 Reissue: Your ShikshaSetu Certificate",
                "Dear learner,\n\nThis is a reissue of your certificate for completing the course: " +
                        cert.getCourse().getTitle() +
                        ".\n\nCertificate Code: " + cert.getCertificateCode() +
                        "\nIssued on: " + cert.getIssuedDate() +
                        "\n\nKeep learning!\nTeam ShikshaSetu"
        );

        return ResponseEntity.ok("Certificate reissued via email.");
    }

    @GetMapping("/certificate/download/{courseId}")
    public ResponseEntity<InputStreamResource> downloadCertificate(
        @PathVariable Long courseId,
        Principal principal
    ){
    User user = userRepo.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Certificate cert = certificateService.getCertificate(user, courseId);

    ByteArrayOutputStream pdf = PdfGeneratorUtil.generateCertificatePDF(cert);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=certificate-" + cert.getCertificateCode() + ".pdf");

    return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(new ByteArrayInputStream(pdf.toByteArray())));
    }
}
