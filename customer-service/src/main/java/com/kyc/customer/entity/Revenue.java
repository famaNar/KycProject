package com.kyc.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "revenues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Revenue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "revenu_annuel")
    private BigDecimal revenuAnnuel;
    
    @Column(name = "source_revenu")
    private String sourceRevenu;
    
    @Column(name = "autres_sources")
    private String autresSources;
    
    @Column(name = "patrimoine_estime")
    private BigDecimal patrimoineEstime;
}
