package com.kyc.customer.dto;

import com.kyc.customer.entity.StatutCompte;
import com.kyc.customer.entity.TypeCompte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteResponse {
    private Long id;
    private String numeroCompte;
    private String intitule;
    private TypeCompte typeCompte;
    private BigDecimal solde;
    private BigDecimal plafond;
    private StatutCompte statutCompte;
    private LocalDateTime dateCreation;
    private LocalDateTime dateFermeture;
    private Long clientId;
    private String devise;
    private String iban;
    private String bic;
    private String description;
}
