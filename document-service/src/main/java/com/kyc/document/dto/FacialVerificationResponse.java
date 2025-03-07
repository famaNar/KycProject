package com.kyc.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacialVerificationResponse {
    
    private Long id;
    private Long clientId;
    private Long idPhotoDocumentId;
    private boolean matchResult;
    private Double matchScore;
    private LocalDateTime verificationDate;
    private String verificationComment;
}
