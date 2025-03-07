package com.kyc.customer.service;

import com.kyc.customer.dto.ClientRegistrationRequest;
import com.kyc.customer.dto.ClientResponse;
import com.kyc.customer.dto.KycFormRequest;
import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.Revenue;
import com.kyc.customer.entity.StatutKYC;
import com.kyc.customer.repository.ClientRepository;
import com.kyc.customer.repository.RevenueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final RevenueRepository revenueRepository;
    private final ClientEventService clientEventService;
    private final EmailService emailService;

    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToClientResponse)
                .collect(Collectors.toList());
    }

    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + id));
        return mapToClientResponse(client);
    }

    public ClientResponse getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'email: " + email));
        return mapToClientResponse(client);
    }

    @Transactional
    public ClientResponse registerClient(ClientRegistrationRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }

        Client client = new Client();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setDateCreation(LocalDateTime.now());
        client.setDateModification(LocalDateTime.now());
        client.setStatutKYC(StatutKYC.EN_ATTENTE);

        Client savedClient = clientRepository.save(client);
        
        // Publier l'événement d'inscription
        clientEventService.publishClientRegistered(savedClient);
        
        // Envoyer un email de bienvenue avec le lien vers le formulaire KYC
        emailService.sendWelcomeEmail(savedClient);

        return mapToClientResponse(savedClient);
    }

    @Transactional
    public ClientResponse submitKycForm(KycFormRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + request.getClientId()));

        // Mettre à jour les informations du client
        client.setDateNaissance(request.getDateNaissance());
        client.setLieuNaissance(request.getLieuNaissance());
        client.setNationalite(request.getNationalite());
        client.setProfession(request.getProfession());
        client.setAdresse(request.getAdresse());
        client.setCodePostal(request.getCodePostal());
        client.setVille(request.getVille());
        client.setPays(request.getPays());
        client.setKycSoumis(true);
        client.setStatutKYC(StatutKYC.SOUMIS);
        client.setDateModification(LocalDateTime.now());

        Client updatedClient = clientRepository.save(client);

        // Enregistrer les informations de revenus
        Revenue revenue = revenueRepository.findByClientId(client.getId())
                .orElse(new Revenue());
        
        revenue.setClient(updatedClient);
        revenue.setRevenuAnnuel(request.getRevenuAnnuel());
        revenue.setSourceRevenu(request.getSourceRevenu());
        revenue.setAutresSources(request.getAutresSources());
        revenue.setPatrimoineEstime(request.getPatrimoineEstime());
        
        revenueRepository.save(revenue);

        // Publier l'événement de soumission du formulaire KYC
        clientEventService.publishKycFormSubmitted(updatedClient);

        return mapToClientResponse(updatedClient);
    }

    @Transactional
    public ClientResponse approveKyc(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + clientId));

        client.setStatutKYC(StatutKYC.APPROUVE);
        client.setDateModification(LocalDateTime.now());
        
        Client updatedClient = clientRepository.save(client);
        
        // Publier l'événement d'approbation KYC
        clientEventService.publishKycApproved(updatedClient);
        
        // Envoyer un email de confirmation
        emailService.sendKycApprovedEmail(updatedClient);

        return mapToClientResponse(updatedClient);
    }

    @Transactional
    public ClientResponse rejectKyc(Long clientId, String reason) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + clientId));

        client.setStatutKYC(StatutKYC.REJETE);
        client.setRaisonRejet(reason);
        client.setDateModification(LocalDateTime.now());
        
        Client updatedClient = clientRepository.save(client);
        
        // Publier l'événement de rejet KYC
        clientEventService.publishKycRejected(updatedClient, reason);
        
        // Envoyer un email de rejet
        emailService.sendKycRejectedEmail(updatedClient, reason);

        return mapToClientResponse(updatedClient);
    }

    private ClientResponse mapToClientResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getEmail(),
                client.getTelephone(),
                client.getDateNaissance(),
                client.getLieuNaissance(),
                client.getNationalite(),
                client.getProfession(),
                client.getAdresse(),
                client.getCodePostal(),
                client.getVille(),
                client.getPays(),
                client.getStatutKYC(),
                client.getDateCreation(),
                client.isKycSoumis(),
                client.isVerificationFacialeEffectuee(),
                client.isDocumentsSoumis()
        );
    }
}
