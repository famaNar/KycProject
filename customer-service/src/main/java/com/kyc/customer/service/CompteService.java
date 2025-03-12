package com.kyc.customer.service;

import com.kyc.customer.dto.CompteRequest;
import com.kyc.customer.dto.CompteResponse;
import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.Compte;
import com.kyc.customer.entity.StatutCompte;
import com.kyc.customer.entity.StatutKYC;
import com.kyc.customer.exception.ResourceNotFoundException;
import com.kyc.customer.repository.ClientRepository;
import com.kyc.customer.repository.CompteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final ClientEventService clientEventService;

    @Transactional
    public CompteResponse creerCompte(Long clientId, CompteRequest request) {
        // Vérifier que le client existe
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId));
        
        // Vérifier que le KYC est approuvé
        if (client.getStatutKYC() != StatutKYC.APPROUVE) {
            throw new IllegalStateException("Le client doit avoir un KYC approuvé pour créer un compte");
        }
        
        // Créer le nouveau compte
        Compte compte = new Compte();
        compte.setIntitule(request.getIntitule());
        compte.setTypeCompte(request.getTypeCompte());
        compte.setPlafond(request.getPlafond());
        compte.setDevise(request.getDevise());
        compte.setDescription(request.getDescription());
        compte.setClient(client);
        
        // Générer un numéro de compte unique
        compte.setNumeroCompte(genererNumeroCompte());
        
        // Générer IBAN et BIC (simplifiés pour l'exemple)
        compte.setIban("FR" + UUID.randomUUID().toString().substring(0, 27).toUpperCase());
        compte.setBic("KYCBFRPP");
        
        // Sauvegarder le compte
        Compte savedCompte = compteRepository.save(compte);
        
        // Publier un événement de création de compte
        clientEventService.publishClientAccountCreated(client, savedCompte);
        
        return mapToCompteResponse(savedCompte);
    }
    
    @Transactional(readOnly = true)
    public List<CompteResponse> getComptesClient(Long clientId) {
        // Vérifier que le client existe
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId);
        }
        
        return compteRepository.findByClientId(clientId).stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CompteResponse> getComptesActifsClient(Long clientId) {
        // Vérifier que le client existe
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client non trouvé avec l'ID: " + clientId);
        }
        
        return compteRepository.findByClientIdAndStatutCompte(clientId, StatutCompte.ACTIF).stream()
                .map(this::mapToCompteResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CompteResponse getCompteById(Long compteId) {
        return compteRepository.findById(compteId)
                .map(this::mapToCompteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + compteId));
    }
    
    @Transactional(readOnly = true)
    public CompteResponse getCompteByNumero(String numeroCompte) {
        return compteRepository.findByNumeroCompte(numeroCompte)
                .map(this::mapToCompteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec le numéro: " + numeroCompte));
    }
    
    @Transactional
    public CompteResponse updateStatutCompte(Long compteId, StatutCompte nouveauStatut) {
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + compteId));
        
        compte.setStatutCompte(nouveauStatut);
        
        // Si le compte est fermé, enregistrer la date de fermeture
        if (nouveauStatut == StatutCompte.FERME) {
            compte.setDateFermeture(LocalDateTime.now());
        }
        
        Compte updatedCompte = compteRepository.save(compte);
        
        // Publier un événement de mise à jour de statut de compte
        clientEventService.publishClientAccountStatusUpdated(compte.getClient(), updatedCompte);
        
        return mapToCompteResponse(updatedCompte);
    }
    
    @Transactional
    public CompteResponse crediterCompte(Long compteId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à créditer doit être positif");
        }
        
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + compteId));
        
        if (compte.getStatutCompte() != StatutCompte.ACTIF) {
            throw new IllegalStateException("Impossible de créditer un compte qui n'est pas actif");
        }
        
        // Créditer le compte
        compte.setSolde(compte.getSolde().add(montant));
        
        Compte updatedCompte = compteRepository.save(compte);
        
        // Publier un événement de crédit de compte
        clientEventService.publishClientAccountCredited(compte.getClient(), updatedCompte, montant);
        
        return mapToCompteResponse(updatedCompte);
    }
    
    @Transactional
    public CompteResponse debiterCompte(Long compteId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant à débiter doit être positif");
        }
        
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé avec l'ID: " + compteId));
        
        if (compte.getStatutCompte() != StatutCompte.ACTIF) {
            throw new IllegalStateException("Impossible de débiter un compte qui n'est pas actif");
        }
        
        // Vérifier que le solde est suffisant
        if (compte.getSolde().compareTo(montant) < 0) {
            throw new IllegalStateException("Solde insuffisant pour effectuer cette opération");
        }
        
        // Débiter le compte
        compte.setSolde(compte.getSolde().subtract(montant));
        
        Compte updatedCompte = compteRepository.save(compte);
        
        // Publier un événement de débit de compte
        clientEventService.publishClientAccountDebited(compte.getClient(), updatedCompte, montant);
        
        return mapToCompteResponse(updatedCompte);
    }
    
    // Méthode utilitaire pour générer un numéro de compte unique
    private String genererNumeroCompte() {
        return "KYC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Méthode utilitaire pour mapper un Compte vers un CompteResponse
    private CompteResponse mapToCompteResponse(Compte compte) {
        CompteResponse response = new CompteResponse();
        response.setId(compte.getId());
        response.setNumeroCompte(compte.getNumeroCompte());
        response.setIntitule(compte.getIntitule());
        response.setTypeCompte(compte.getTypeCompte());
        response.setSolde(compte.getSolde());
        response.setPlafond(compte.getPlafond());
        response.setStatutCompte(compte.getStatutCompte());
        response.setDateCreation(compte.getDateCreation());
        response.setDateFermeture(compte.getDateFermeture());
        response.setClientId(compte.getClient().getId());
        response.setDevise(compte.getDevise());
        response.setIban(compte.getIban());
        response.setBic(compte.getBic());
        response.setDescription(compte.getDescription());
        return response;
    }
}
