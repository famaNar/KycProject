package com.kyc.notification.controller;

import com.kyc.notification.model.ClientEvent;
import com.kyc.notification.model.EventType;
import com.kyc.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    
    // Nouveaux endpoints de test pour les notifications liées aux comptes
    
    @PostMapping("/test/account-created")
    public ResponseEntity<String> testAccountCreatedEmail(
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam Long clientId,
            @RequestParam Long compteId,
            @RequestParam String numeroCompte,
            @RequestParam String intituleCompte,
            @RequestParam String typeCompte) {
        
        ClientEvent event = new ClientEvent();
        event.setClientId(clientId);
        event.setEmail(email);
        event.setFullName(fullName);
        event.setEventType(EventType.ACCOUNT_CREATED);
        event.setTimestamp(LocalDateTime.now());
        event.setCompteId(compteId);
        event.setNumeroCompte(numeroCompte);
        event.setIntituleCompte(intituleCompte);
        event.setTypeCompte(typeCompte);
        event.setSolde(BigDecimal.ZERO);
        event.setDevise("EUR");
        event.setDetails("IBAN: FR76 1234 5678 9012 3456 7890 123");
        
        emailService.sendAccountCreatedEmail(event);
        return ResponseEntity.ok("Email de création de compte envoyé avec succès à " + email);
    }
    
    @PostMapping("/test/account-status-update")
    public ResponseEntity<String> testAccountStatusUpdateEmail(
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam Long clientId,
            @RequestParam Long compteId,
            @RequestParam String numeroCompte,
            @RequestParam String intituleCompte,
            @RequestParam String statutCompte) {
        
        ClientEvent event = new ClientEvent();
        event.setClientId(clientId);
        event.setEmail(email);
        event.setFullName(fullName);
        event.setEventType(EventType.ACCOUNT_STATUS_UPDATED);
        event.setTimestamp(LocalDateTime.now());
        event.setCompteId(compteId);
        event.setNumeroCompte(numeroCompte);
        event.setIntituleCompte(intituleCompte);
        event.setStatutCompte(statutCompte);
        
        emailService.sendAccountStatusUpdatedEmail(event);
        return ResponseEntity.ok("Email de mise à jour de statut de compte envoyé avec succès à " + email);
    }
    
    @PostMapping("/test/account-transaction")
    public ResponseEntity<String> testAccountTransactionEmail(
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam Long clientId,
            @RequestParam Long compteId,
            @RequestParam String numeroCompte,
            @RequestParam String intituleCompte,
            @RequestParam BigDecimal montant,
            @RequestParam BigDecimal solde,
            @RequestParam(defaultValue = "true") boolean isCredit) {
        
        ClientEvent event = new ClientEvent();
        event.setClientId(clientId);
        event.setEmail(email);
        event.setFullName(fullName);
        event.setEventType(isCredit ? EventType.ACCOUNT_CREDITED : EventType.ACCOUNT_DEBITED);
        event.setTimestamp(LocalDateTime.now());
        event.setCompteId(compteId);
        event.setNumeroCompte(numeroCompte);
        event.setIntituleCompte(intituleCompte);
        event.setMontant(montant);
        event.setSolde(solde);
        event.setDevise("EUR");
        
        emailService.sendTransactionEmail(event);
        String transactionType = isCredit ? "crédit" : "débit";
        return ResponseEntity.ok("Email de " + transactionType + " de compte envoyé avec succès à " + email);
    }
}
