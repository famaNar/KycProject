package com.kyc.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comptes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numeroCompte;
    
    @Column(nullable = false)
    private String intitule;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCompte typeCompte;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal plafond;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCompte statutCompte = StatutCompte.ACTIF;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column
    private LocalDateTime dateFermeture;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column
    private String devise = "EUR";
    
    @Column
    private String iban;
    
    @Column
    private String bic;
    
    @Column
    private String description;
}
