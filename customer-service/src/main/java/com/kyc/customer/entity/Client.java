package com.kyc.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String telephone;
    
    @Column(name = "date_naissance")
    private LocalDateTime dateNaissance;
    
    @Column(name = "lieu_naissance")
    private String lieuNaissance;
    
    @Column
    private String nationalite;
    
    @Column
    private String profession;
    
    @Column
    private String adresse;
    
    @Column(name = "code_postal")
    private String codePostal;
    
    @Column
    private String ville;
    
    @Column
    private String pays;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_kyc")
    private StatutKYC statutKYC = StatutKYC.EN_ATTENTE;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();
    
    @Column(name = "kyc_soumis")
    private boolean kycSoumis = false;
    
    @Column(name = "verification_faciale_effectuee")
    private boolean verificationFacialeEffectuee = false;
    
    @Column(name = "documents_soumis")
    private boolean documentsSoumis = false;
    
    @Column(name = "raison_rejet")
    private String raisonRejet;
}
