package com.kyc.customer.dto;

import com.kyc.customer.entity.TypeCompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompteRequest {
    
    @NotBlank(message = "L'intitulé du compte est obligatoire")
    private String intitule;
    
    @NotNull(message = "Le type de compte est obligatoire")
    private TypeCompte typeCompte;
    
    @Positive(message = "Le plafond doit être un nombre positif")
    private BigDecimal plafond;
    
    private String devise = "EUR";
    
    private String description;
}
