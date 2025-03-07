package com.kyc.customer.dto;

import com.kyc.customer.entity.StatutKYC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDateTime dateNaissance;
    private String lieuNaissance;
    private String nationalite;
    private String profession;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;
    private StatutKYC statutKYC;
    private LocalDateTime dateCreation;
    private boolean kycSoumis;
    private boolean verificationFacialeEffectuee;
    private boolean documentsSoumis;
}
