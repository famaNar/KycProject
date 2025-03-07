package com.kyc.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycFormRequest {
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;
    
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDateTime dateNaissance;
    
    private String lieuNaissance;
    
    private String nationalite;
    
    private String profession;
    
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;
    
    private String codePostal;
    
    private String ville;
    
    private String pays;
    
    // Informations sur les revenus
    private BigDecimal revenuAnnuel;
    
    private String sourceRevenu;
    
    private String autresSources;
    
    private BigDecimal patrimoineEstime;
}
