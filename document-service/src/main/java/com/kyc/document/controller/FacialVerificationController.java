package com.kyc.document.controller;

import com.kyc.document.dto.FacialVerificationResponse;
import com.kyc.document.service.FacialVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/facial-verification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FacialVerificationController {

    private final FacialVerificationService facialVerificationService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<FacialVerificationResponse>> getVerificationsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(facialVerificationService.getVerificationsByClientId(clientId));
    }

    @GetMapping("/client/{clientId}/latest")
    public ResponseEntity<FacialVerificationResponse> getLatestVerification(@PathVariable Long clientId) {
        return ResponseEntity.ok(facialVerificationService.getLatestVerification(clientId));
    }

    @PostMapping("/verify")
    public ResponseEntity<FacialVerificationResponse> performFacialVerification(
            @RequestParam("clientId") Long clientId,
            @RequestParam("selfie") MultipartFile selfieFile) {
        
        FacialVerificationResponse verification = facialVerificationService.performFacialVerification(clientId, selfieFile);
        return new ResponseEntity<>(verification, HttpStatus.CREATED);
    }

    @GetMapping("/client/{clientId}/status")
    public ResponseEntity<Boolean> hasPassedFacialVerification(@PathVariable Long clientId) {
        return ResponseEntity.ok(facialVerificationService.hasPassedFacialVerification(clientId));
    }
}
