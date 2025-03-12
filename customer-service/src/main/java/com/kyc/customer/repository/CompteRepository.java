package com.kyc.customer.repository;

import com.kyc.customer.entity.Compte;
import com.kyc.customer.entity.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    List<Compte> findByClientId(Long clientId);
    List<Compte> findByClientIdAndStatutCompte(Long clientId, StatutCompte statutCompte);
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    boolean existsByNumeroCompte(String numeroCompte);
    boolean existsByIban(String iban);
}
