package com.shikshasetu.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
// import jakarta.mail.MessagingException;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("shikshasetu87@gmail.com"); 
        
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Sending email to: " + to + " with subject: " + subject);
    }

    public MimeMessage createMimeMessage() {
        return mailSender.createMimeMessage();
    }

    public void sendMimeMessage(MimeMessage message) {
        mailSender.send(message);
    }
}
