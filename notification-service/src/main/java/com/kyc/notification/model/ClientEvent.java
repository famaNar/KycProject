package com.kyc.notification.model;

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
    private LocalDateTime timestamp;
}
