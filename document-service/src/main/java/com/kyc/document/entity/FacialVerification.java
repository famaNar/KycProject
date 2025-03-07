package com.kyc.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "facial_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacialVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long clientId;
    
    @Column(nullable = false)
    private String selfieFileName;
    
    @Column(nullable = false)
    private String selfiePath;
    
    @Column(name = "id_photo_document_id")
    private Long idPhotoDocumentId;
    
    @Column(nullable = false)
    private boolean matchResult;
    
    @Column
    private Double matchScore;
    
    @Column(nullable = false)
    private LocalDateTime verificationDate = LocalDateTime.now();
    
    @Column
    private String verificationComment;
}
