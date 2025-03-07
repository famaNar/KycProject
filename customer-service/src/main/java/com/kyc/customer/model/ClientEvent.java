package com.kyc.customer.model;

import com.kyc.customer.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    
    public ClientEvent(Long clientId, String email, String fullName, EventType eventType, String details) {
        this.clientId = clientId;
        this.email = email;
        this.fullName = fullName;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
