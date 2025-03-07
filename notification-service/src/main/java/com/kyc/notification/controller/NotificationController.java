package com.kyc.notification.controller;

import com.kyc.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/test/welcome")
    public ResponseEntity<String> testWelcomeEmail(
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam Long clientId) {
        
        emailService.sendWelcomeEmail(email, fullName, clientId);
        return ResponseEntity.ok("Email de bienvenue envoyé avec succès à " + email);
    }

    @PostMapping("/test/kyc-submitted")
    public ResponseEntity<String> testKycSubmittedEmail(
            @RequestParam String email,
            @RequestParam String fullName) {
        
        emailService.sendKycSubmittedEmail(email, fullName);
        return ResponseEntity.ok("Email de confirmation de soumission KYC envoyé avec succès à " + email);
    }

    @PostMapping("/test/kyc-approved")
    public ResponseEntity<String> testKycApprovedEmail(
            @RequestParam String email,
            @RequestParam String fullName) {
        
        emailService.sendKycApprovedEmail(email, fullName);
        return ResponseEntity.ok("Email d'approbation KYC envoyé avec succès à " + email);
    }

    @PostMapping("/test/kyc-rejected")
    public ResponseEntity<String> testKycRejectedEmail(
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam String reason) {
        
        emailService.sendKycRejectedEmail(email, fullName, reason);
        return ResponseEntity.ok("Email de rejet KYC envoyé avec succès à " + email);
    }
}
