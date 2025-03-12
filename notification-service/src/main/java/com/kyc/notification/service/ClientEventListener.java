package com.kyc.notification.service;

import com.kyc.notification.model.ClientEvent;
import com.kyc.notification.model.EventType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ClientEventListener.class);

    private final EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topics.client-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleClientEvent(ClientEvent event) {
        logger.info("Événement client reçu : {}", event);
        
        try {
            switch (event.getEventType()) {
                case CLIENT_REGISTERED:
                    logger.info("Traitement de l'événement d'inscription client : {}", event);
                    emailService.sendWelcomeEmail(event.getEmail(), event.getFullName(), event.getClientId());
                    break;
                    
                case KYC_FORM_SUBMITTED:
                    logger.info("Traitement de l'événement de soumission KYC : {}", event);
                    emailService.sendKycSubmittedEmail(event.getEmail(), event.getFullName());
                    break;
                    
                case KYC_APPROVED:
                    logger.info("Traitement de l'événement d'approbation KYC : {}", event);
                    emailService.sendKycApprovedEmail(event.getEmail(), event.getFullName());
                    break;
                    
                case KYC_REJECTED:
                    logger.info("Traitement de l'événement de rejet KYC : {}", event);
                    emailService.sendKycRejectedEmail(event.getEmail(), event.getFullName(), event.getDetails());
                    break;
                
                // Nouveaux cas pour les événements liés aux comptes
                case ACCOUNT_CREATED:
                    logger.info("Traitement de l'événement de création de compte : {}", event);
                    emailService.sendAccountCreatedEmail(event);
                    break;
                    
                case ACCOUNT_STATUS_UPDATED:
                    logger.info("Traitement de l'événement de mise à jour de statut de compte : {}", event);
                    emailService.sendAccountStatusUpdateEmail(event);
                    break;
                    
                case ACCOUNT_CREDITED:
                    logger.info("Traitement de l'événement de crédit de compte : {}", event);
                    emailService.sendAccountTransactionEmail(event, true);
                    break;
                    
                case ACCOUNT_DEBITED:
                    logger.info("Traitement de l'événement de débit de compte : {}", event);
                    emailService.sendAccountTransactionEmail(event, false);
                    break;
                    
                default:
                    logger.warn("Type d'événement non pris en charge : {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de l'événement client : {}", e.getMessage(), e);
        }
    }
}
