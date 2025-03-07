package com.kyc.customer.service;

import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.EventType;
import com.kyc.customer.model.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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

    private void publishEvent(ClientEvent event) {
        try {
            logger.info("Publication d'un événement client: {}", event);
            kafkaTemplate.send(clientEventsTopic, event);
        } catch (Exception e) {
            logger.error("Erreur lors de la publication d'un événement client: {}", e.getMessage(), e);
        }
    }
}
