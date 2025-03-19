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

    @KafkaListener(topics = "${app.kafka.topics.client-events}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
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
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            emailSender.send(message);
            log.info("Email envoyé avec succès à : {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage());
            throw e;
        }
    }
}
