package com.kyc.customer.service;

import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.Compte;
import com.kyc.customer.entity.EventType;
import com.kyc.customer.model.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class ClientEventService {

    private static final Logger logger = LoggerFactory.getLogger(ClientEventService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.client-events}")
    private String clientEventsTopic;

    public ClientEventService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishClientRegistered(Client client) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.CLIENT_REGISTERED,
                null
        );
        publishEvent(event);
    }

    public void publishKycFormSubmitted(Client client) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.KYC_FORM_SUBMITTED,
                null
        );
        publishEvent(event);
    }

    public void publishKycApproved(Client client) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.KYC_APPROVED,
                null
        );
        publishEvent(event);
    }

    public void publishKycRejected(Client client, String reason) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.KYC_REJECTED,
                reason
        );
        publishEvent(event);
    }
    
    public void publishClientAccountCreated(Client client, Compte compte) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.ACCOUNT_CREATED,
                "Nouveau compte créé: " + compte.getIntitule()
        );
        
        event.setCompteId(compte.getId());
        event.setNumeroCompte(compte.getNumeroCompte());
        event.setIntituleCompte(compte.getIntitule());
        event.setTypeCompte(compte.getTypeCompte().name());
        event.setStatutCompte(compte.getStatutCompte().name());
        event.setSolde(compte.getSolde());
        event.setDevise(compte.getDevise());
        
        publishEvent(event);
    }
    
    public void publishClientAccountStatusUpdated(Client client, Compte compte) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.ACCOUNT_STATUS_UPDATED,
                "Statut du compte mis à jour: " + compte.getStatutCompte().name()
        );
        
        event.setCompteId(compte.getId());
        event.setNumeroCompte(compte.getNumeroCompte());
        event.setIntituleCompte(compte.getIntitule());
        event.setTypeCompte(compte.getTypeCompte().name());
        event.setStatutCompte(compte.getStatutCompte().name());
        event.setSolde(compte.getSolde());
        event.setDevise(compte.getDevise());
        
        publishEvent(event);
    }
    
    public void publishClientAccountCredited(Client client, Compte compte, BigDecimal montant) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.ACCOUNT_CREDITED,
                "Compte crédité de " + montant + " " + compte.getDevise()
        );
        
        event.setCompteId(compte.getId());
        event.setNumeroCompte(compte.getNumeroCompte());
        event.setIntituleCompte(compte.getIntitule());
        event.setTypeCompte(compte.getTypeCompte().name());
        event.setStatutCompte(compte.getStatutCompte().name());
        event.setMontant(montant);
        event.setSolde(compte.getSolde());
        event.setDevise(compte.getDevise());
        
        publishEvent(event);
    }
    
    public void publishClientAccountDebited(Client client, Compte compte, BigDecimal montant) {
        String fullName = client.getPrenom() + " " + client.getNom();
        ClientEvent event = new ClientEvent(
                client.getId(),
                client.getEmail(),
                fullName,
                EventType.ACCOUNT_DEBITED,
                "Compte débité de " + montant + " " + compte.getDevise()
        );
        
        event.setCompteId(compte.getId());
        event.setNumeroCompte(compte.getNumeroCompte());
        event.setIntituleCompte(compte.getIntitule());
        event.setTypeCompte(compte.getTypeCompte().name());
        event.setStatutCompte(compte.getStatutCompte().name());
        event.setMontant(montant);
        event.setSolde(compte.getSolde());
        event.setDevise(compte.getDevise());
        
        publishEvent(event);
    }

    private void publishEvent(ClientEvent event) {
        try {
            logger.info("Début de la publication d'un événement client: {}", event);
            logger.info("Topic Kafka cible: {}", clientEventsTopic);
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(clientEventsTopic, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Événement client publié avec succès: topic={}, partition={}, offset={}", 
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    logger.error("Erreur lors de la publication de l'événement client: {}", ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Exception lors de la publication d'un événement client: {}", e.getMessage(), e);
            logger.error("Détails de l'événement qui a échoué: {}", event);
            throw new RuntimeException("Erreur lors de la publication de l'événement client", e);
        }
    }
}
