package com.shikshasetu.backend.service;

import java.io.ByteArrayOutputStream;
import com.shikshasetu.backend.model.*;
import com.shikshasetu.backend.repository.*;
import com.shikshasetu.backend.util.PdfGeneratorUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private VideoContentRepository videoContentRepo;

    @Autowired
    private VideoWatchHistoryRepository watchHistoryRepo;

    @Autowired
    private CertificateRepository certificateRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

    public Certificate generateCertificate(User user, Long courseId) throws MessagingException{
        // 🔐 Step 1: Confirm user is enrolled
        Course course = enrollmentRepo.findByStudentIdAndCourseId(user.getId(), courseId)
                .map(Enrollment::getCourse)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course."));

        // 🧮 Step 2: Get total video count for course
        long totalVideos = videoContentRepo.countByCourseId(courseId);

        // 👀 Step 3: Get how many videos the user has watched
        long watchedCount = watchHistoryRepo.countByUserIdAndVideo_Course_Id(user.getId(), courseId);

        // ✅ Step 4: Check if all videos watched
        if (watchedCount < totalVideos) {
            throw new RuntimeException("Please complete all videos before generating certificate.");
        }

        // 🧾 Step 5: Check if certificate already issued
        Optional<Certificate> existing = certificateRepo.findByUserIdAndCourseId(user.getId(), courseId);
        if (existing.isPresent()) return existing.get();

        // 🪪 Step 6: Generate new certificate
        Certificate cert = new Certificate();
        cert.setUser(user);
        cert.setCourse(course);
        cert.setIssuedDate(LocalDate.now());
        cert.setCertificateCode("CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // ✅Send email after certificate is issued
        // Generate PDF
        ByteArrayOutputStream pdfStream = PdfGeneratorUtil.generateCertificatePDF(cert);
        
        // Prepare email with attachment
        MimeMessage message = emailService.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("📜 Your Certificate for " + course.getTitle());
        helper.setText("Hi " + user.getName() + ",\n\n" +
        "Attached is your certificate for completing the course: " + course.getTitle() + "\n\n" +
        "Regards,\nTeam ShikshaSetu");
        helper.addAttachment("certificate.pdf", new ByteArrayResource(pdfStream.toByteArray()));
        emailService.sendMimeMessage(message);
        return certificateRepo.save(cert);
    }

    public Certificate getCertificate(User user, Long courseId) {
    return certificateRepo.findByUserIdAndCourseId(user.getId(), courseId)
            .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public void expireOldSubscriptions() {
    List<Subscription> subscriptions = subscriptionRepository.findAll();
    for (Subscription s : subscriptions) {
        if (s.getEndDate().isBefore(LocalDate.now())) {
            s.setActive(false);
            subscriptionRepository.save(s);

            // Send email notification
            emailService.sendEmail(
                s.getUser().getEmail(),
                "Your Subscription Expired",
                "Hello " + s.getUser().getName() + ",\n\nYour ShikshaSetu subscription has expired. Please renew to continue accessing premium courses."
            );
        }
        }
    }
    
    public Certificate getCertificateForUser(String email, Long courseId) {
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return certificateRepo.findByUserIdAndCourseId(user.getId(), courseId)
            .orElse(null);
    }
}
