package com.kyc.document.service;

import com.kyc.document.dto.DocumentResponse;
import com.kyc.document.entity.Document;
import com.kyc.document.entity.DocumentType;
import com.kyc.document.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    @Value("${app.document.upload-dir}")
    private String uploadDir;

    public List<DocumentResponse> getDocumentsByClientId(Long clientId) {
        return documentRepository.findByClientId(clientId).stream()
                .map(this::mapToDocumentResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé avec l'ID: " + id));
        return mapToDocumentResponse(document);
    }

    @Transactional
    public DocumentResponse uploadDocument(Long clientId, MultipartFile file, 
                                         DocumentType documentType, String description) {
        try {
            // Créer le répertoire de téléchargement s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // Chemin complet du fichier
            Path filePath = uploadPath.resolve(fileName);
            
            // Copier le fichier dans le répertoire de téléchargement
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'entité Document
            Document document = new Document();
            document.setClientId(clientId);
            document.setFileName(fileName);
            document.setOriginalFileName(originalFileName);
            document.setFilePath(filePath.toString());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setDocumentType(documentType);
            document.setDescription(description);
            document.setUploadDate(LocalDateTime.now());
            
            Document savedDocument = documentRepository.save(document);
            
            logger.info("Document téléchargé avec succès: {}", savedDocument.getFileName());
            
            return mapToDocumentResponse(savedDocument);
            
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement du document: {}", e.getMessage(), e);
            throw new RuntimeException("Échec du téléchargement du document: " + e.getMessage());
        }
    }

    @Transactional
    public DocumentResponse verifyDocument(Long documentId, boolean verified, String comment) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé avec l'ID: " + documentId));
        
        document.setVerified(verified);
        document.setVerificationDate(LocalDateTime.now());
        document.setVerificationComment(comment);
        
        Document updatedDocument = documentRepository.save(document);
        
        logger.info("Document vérifié: {}, Résultat: {}", documentId, verified);
        
        return mapToDocumentResponse(updatedDocument);
    }

    public byte[] getDocumentContent(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé avec l'ID: " + documentId));
        
        try {
            Path filePath = Paths.get(document.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du contenu du document: {}", e.getMessage(), e);
            throw new RuntimeException("Échec de la lecture du contenu du document: " + e.getMessage());
        }
    }

    private DocumentResponse mapToDocumentResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getClientId(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getDocumentType(),
                document.getUploadDate(),
                document.getDescription(),
                document.isVerified(),
                document.getVerificationDate(),
                document.getVerificationComment()
        );
    }
}
