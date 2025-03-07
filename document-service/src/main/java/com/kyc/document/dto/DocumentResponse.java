package com.kyc.document.dto;

import com.kyc.document.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    
    private Long id;
    private Long clientId;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private DocumentType documentType;
    private LocalDateTime uploadDate;
    private String description;
    private boolean verified;
    private LocalDateTime verificationDate;
    private String verificationComment;
}
