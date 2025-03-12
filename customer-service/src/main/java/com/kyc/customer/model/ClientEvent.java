package com.kyc.customer.model;

import com.kyc.customer.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEvent {
    
    private Long clientId;
    private String email;
    private String fullName;
    private EventType eventType;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // Informations sur le compte (pour les événements liés aux comptes)
    private Long compteId;
    private String numeroCompte;
    private String intituleCompte;
    private String typeCompte;
    private String statutCompte;
    private BigDecimal montant;
    private BigDecimal solde;
    private String devise;
    
    public ClientEvent(Long clientId, String email, String fullName, EventType eventType, String details) {
        this.clientId = clientId;
        this.email = email;
        this.fullName = fullName;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
