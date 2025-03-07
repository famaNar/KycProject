package com.kyc.document.repository;

import com.kyc.document.entity.Document;
import com.kyc.document.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByClientId(Long clientId);
    
    List<Document> findByClientIdAndDocumentType(Long clientId, DocumentType documentType);
    
    Optional<Document> findByClientIdAndDocumentTypeAndVerifiedTrue(Long clientId, DocumentType documentType);
}
