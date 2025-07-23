package com.shikshasetu.backend.controller;

import com.shikshasetu.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-test")
    public String sendTestEmail(@RequestParam String to) {
        emailService.sendEmail(to, "Test Email from ShikshaSetu", "Welcome to ShikshaSetu!");
        return "Test email sent to " + to;
    }
}
