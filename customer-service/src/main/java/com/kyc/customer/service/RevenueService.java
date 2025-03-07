package com.kyc.customer.service;

import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.Revenue;
import com.kyc.customer.repository.ClientRepository;
import com.kyc.customer.repository.RevenueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final ClientRepository clientRepository;

    public List<Revenue> getRevenuesByClientId(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + clientId));
        
        return revenueRepository.findByClient(client);
    }

    @Transactional
    public Revenue saveRevenue(Long clientId, BigDecimal revenuAnnuel, String sourceRevenu, 
                              String autresSources, BigDecimal patrimoineEstime) {
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + clientId));
        
        Revenue revenue = revenueRepository.findByClientId(clientId)
                .orElse(new Revenue());
        
        revenue.setClient(client);
        revenue.setRevenuAnnuel(revenuAnnuel);
        revenue.setSourceRevenu(sourceRevenu);
        revenue.setAutresSources(autresSources);
        revenue.setPatrimoineEstime(patrimoineEstime);
        
        return revenueRepository.save(revenue);
    }

    public Revenue getRevenueByClientId(Long clientId) {
        return revenueRepository.findByClientId(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Revenus non trouvés pour le client avec l'ID: " + clientId));
    }
}
