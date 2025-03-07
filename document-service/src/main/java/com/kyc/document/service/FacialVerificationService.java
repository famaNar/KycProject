package com.kyc.document.service;

import com.kyc.document.dto.FacialVerificationResponse;
import com.kyc.document.entity.Document;
import com.kyc.document.entity.DocumentType;
import com.kyc.document.entity.FacialVerification;
import com.kyc.document.repository.DocumentRepository;
import com.kyc.document.repository.FacialVerificationRepository;
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
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacialVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(FacialVerificationService.class);

    private final FacialVerificationRepository facialVerificationRepository;
    private final DocumentRepository documentRepository;

    @Value("${app.document.upload-dir}")
    private String uploadDir;

    public List<FacialVerificationResponse> getVerificationsByClientId(Long clientId) {
        return facialVerificationRepository.findByClientId(clientId).stream()
                .map(this::mapToVerificationResponse)
                .collect(Collectors.toList());
    }

    public FacialVerificationResponse getLatestVerification(Long clientId) {
        FacialVerification verification = facialVerificationRepository.findTopByClientIdOrderByVerificationDateDesc(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune vérification faciale trouvée pour le client avec l'ID: " + clientId));
        return mapToVerificationResponse(verification);
    }

    @Transactional
    public FacialVerificationResponse performFacialVerification(Long clientId, MultipartFile selfieFile) {
        try {
            // Vérifier si le client a un document d'identité
            Document idDocument = documentRepository.findByClientIdAndDocumentTypeAndVerifiedTrue(clientId, DocumentType.CARTE_IDENTITE)
                    .orElseGet(() -> documentRepository.findByClientIdAndDocumentTypeAndVerifiedTrue(clientId, DocumentType.PASSEPORT)
                            .orElseThrow(() -> new IllegalStateException("Aucun document d'identité vérifié trouvé pour le client")));

            // Créer le répertoire de téléchargement s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique pour le selfie
            String originalFileName = selfieFile.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = "selfie_" + UUID.randomUUID().toString() + fileExtension;
            
            // Chemin complet du fichier selfie
            Path selfiePath = uploadPath.resolve(fileName);
            
            // Copier le fichier dans le répertoire de téléchargement
            Files.copy(selfieFile.getInputStream(), selfiePath, StandardCopyOption.REPLACE_EXISTING);

            // Simuler une vérification faciale (dans un environnement réel, vous utiliseriez une API de reconnaissance faciale)
            // Pour ce prototype, nous utilisons une vérification aléatoire avec un biais vers le succès
            boolean matchResult = new Random().nextDouble() > 0.3; // 70% de chance de succès
            double matchScore = matchResult ? 0.7 + (new Random().nextDouble() * 0.3) : 0.3 + (new Random().nextDouble() * 0.4);

            // Créer l'entité de vérification faciale
            FacialVerification verification = new FacialVerification();
            verification.setClientId(clientId);
            verification.setSelfieFileName(fileName);
            verification.setSelfiePath(selfiePath.toString());
            verification.setIdPhotoDocumentId(idDocument.getId());
            verification.setMatchResult(matchResult);
            verification.setMatchScore(matchScore);
            verification.setVerificationDate(LocalDateTime.now());
            verification.setVerificationComment(matchResult ? 
                    "Vérification faciale réussie avec un score de confiance de " + String.format("%.2f", matchScore) : 
                    "Échec de la vérification faciale. Score de confiance: " + String.format("%.2f", matchScore));
            
            FacialVerification savedVerification = facialVerificationRepository.save(verification);
            
            logger.info("Vérification faciale effectuée pour le client {}: Résultat={}, Score={}", 
                    clientId, matchResult, String.format("%.2f", matchScore));
            
            return mapToVerificationResponse(savedVerification);
            
        } catch (IOException e) {
            logger.error("Erreur lors du traitement de l'image selfie: {}", e.getMessage(), e);
            throw new RuntimeException("Échec du traitement de l'image selfie: " + e.getMessage());
        }
    }

    public boolean hasPassedFacialVerification(Long clientId) {
        return facialVerificationRepository.existsByClientIdAndMatchResultTrue(clientId);
    }

    private FacialVerificationResponse mapToVerificationResponse(FacialVerification verification) {
        return new FacialVerificationResponse(
                verification.getId(),
                verification.getClientId(),
                verification.getIdPhotoDocumentId(),
                verification.isMatchResult(),
                verification.getMatchScore(),
                verification.getVerificationDate(),
                verification.getVerificationComment()
        );
    }
}
