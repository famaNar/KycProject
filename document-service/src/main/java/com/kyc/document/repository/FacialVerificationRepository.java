package com.kyc.document.repository;

import com.kyc.document.entity.FacialVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacialVerificationRepository extends JpaRepository<FacialVerification, Long> {
    
    List<FacialVerification> findByClientId(Long clientId);
    
    Optional<FacialVerification> findTopByClientIdOrderByVerificationDateDesc(Long clientId);
    
    boolean existsByClientIdAndMatchResultTrue(Long clientId);
}
