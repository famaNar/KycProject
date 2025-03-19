package com.kyc.notification.service;

import com.kyc.notification.model.ClientEvent;
import com.kyc.notification.model.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientEventListener {

    private final JavaMailSender emailSender;
    private final EmailTemplateService emailTemplateService;

    @KafkaListener(topics = "${app.kafka.topics.client-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientEvent(ClientEvent event) {
        log.info("Événement client reçu : {}", event);

        try {
            String emailContent;
            String subject;

            switch (event.getEventType()) {
                case CLIENT_REGISTERED -> {
                    emailContent = emailTemplateService.generateWelcomeEmailContent(event.getFullName(), event.getClientId());
                    subject = "Bienvenue sur notre plateforme KYC";
                }
                case KYC_APPROVED -> {
                    emailContent = emailTemplateService.generateKycApprovalEmailContent(event.getFullName());
                    subject = "KYC Approuvé - Félicitations !";
                }
                case KYC_REJECTED -> {
                    emailContent = emailTemplateService.generateKycRejectionEmailContent(event.getFullName(), event.getDetails());
                    subject = "KYC Non Approuvé - Action Requise";
                }
                case ACCOUNT_CREATED -> {
                    emailContent = emailTemplateService.generateAccountCreatedEmailContent(
                        event.getFullName(),
                        event.getNumeroCompte(),
                        event.getTypeCompte(),
                        event.getDevise()
                    );
                    subject = "Nouveau Compte Créé";
                }
                case ACCOUNT_CREDITED, ACCOUNT_DEBITED -> {
                    emailContent = emailTemplateService.generateTransactionEmailContent(
                        event.getFullName(),
                        event.getNumeroCompte(),
                        event.getMontant(),
                        event.getSolde(),
                        event.getEventType() == EventType.ACCOUNT_CREDITED ? "CREDIT" : "DEBIT",
                        event.getDevise()
                    );
                    subject = event.getEventType() == EventType.ACCOUNT_CREDITED ? 
                             "Dépôt Effectué sur Votre Compte" : 
                             "Retrait Effectué sur Votre Compte";
                }
                case ACCOUNT_STATUS_UPDATED -> {
                    emailContent = emailTemplateService.generateAccountStatusEmailContent(
                        event.getFullName(),
                        event.getNumeroCompte(),
                        event.getDetails() // Le nouveau statut est passé dans details
                    );
                    subject = "Modification du Statut de Votre Compte";
                }
                default -> {
                    log.warn("Type d'événement non géré : {}", event.getEventType());
                    return;
                }
            }

            sendEmail(event.getEmail(), subject, emailContent);
            log.info("Email envoyé avec succès à : {}", event.getEmail());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email pour l'événement : {}", event, e);
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        emailSender.send(message);
    }
}
