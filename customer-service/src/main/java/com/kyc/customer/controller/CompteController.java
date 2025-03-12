package com.kyc.customer.controller;

import com.kyc.customer.dto.CompteRequest;
import com.kyc.customer.dto.CompteResponse;
import com.kyc.customer.entity.StatutCompte;
import com.kyc.customer.service.CompteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/clients/{clientId}/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    public ResponseEntity<CompteResponse> creerCompte(
            @PathVariable Long clientId,
            @Valid @RequestBody CompteRequest request) {
        return new ResponseEntity<>(compteService.creerCompte(clientId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompteResponse>> getComptesClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(compteService.getComptesClient(clientId));
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<CompteResponse>> getComptesActifsClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(compteService.getComptesActifsClient(clientId));
    }

    @GetMapping("/{compteId}")
    public ResponseEntity<CompteResponse> getCompteById(
            @PathVariable Long clientId,
            @PathVariable Long compteId) {
        CompteResponse compte = compteService.getCompteById(compteId);
        // Vérifier que le compte appartient bien au client
        if (!compte.getClientId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(compte);
    }

    @PutMapping("/{compteId}/statut")
    public ResponseEntity<CompteResponse> updateStatutCompte(
            @PathVariable Long clientId,
            @PathVariable Long compteId,
            @RequestParam StatutCompte statut) {
        CompteResponse compte = compteService.getCompteById(compteId);
        // Vérifier que le compte appartient bien au client
        if (!compte.getClientId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(compteService.updateStatutCompte(compteId, statut));
    }

    @PostMapping("/{compteId}/crediter")
    public ResponseEntity<CompteResponse> crediterCompte(
            @PathVariable Long clientId,
            @PathVariable Long compteId,
            @RequestParam BigDecimal montant) {
        CompteResponse compte = compteService.getCompteById(compteId);
        // Vérifier que le compte appartient bien au client
        if (!compte.getClientId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(compteService.crediterCompte(compteId, montant));
    }

    @PostMapping("/{compteId}/debiter")
    public ResponseEntity<CompteResponse> debiterCompte(
            @PathVariable Long clientId,
            @PathVariable Long compteId,
            @RequestParam BigDecimal montant) {
        CompteResponse compte = compteService.getCompteById(compteId);
        // Vérifier que le compte appartient bien au client
        if (!compte.getClientId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(compteService.debiterCompte(compteId, montant));
    }
}
