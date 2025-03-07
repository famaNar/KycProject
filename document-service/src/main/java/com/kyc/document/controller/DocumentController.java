package com.kyc.document.controller;

import com.kyc.document.dto.DocumentResponse;
import com.kyc.document.entity.DocumentType;
import com.kyc.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(documentService.getDocumentsByClientId(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getDocumentContent(@PathVariable Long id) {
        DocumentResponse document = documentService.getDocumentById(id);
        byte[] content = documentService.getDocumentContent(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(document.getContentType()));
        headers.setContentDispositionFormData("attachment", document.getOriginalFileName());
        
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("clientId") Long clientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "description", required = false) String description) {
        
        DocumentResponse uploadedDocument = documentService.uploadDocument(clientId, file, documentType, description);
        return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<DocumentResponse> verifyDocument(
            @PathVariable Long id,
            @RequestParam boolean verified,
            @RequestParam(required = false) String comment) {
        
        return ResponseEntity.ok(documentService.verifyDocument(id, verified, comment));
    }
}
