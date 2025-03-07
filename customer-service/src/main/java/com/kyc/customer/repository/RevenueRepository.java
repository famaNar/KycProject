package com.kyc.customer.repository;

import com.kyc.customer.entity.Client;
import com.kyc.customer.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    
    List<Revenue> findByClient(Client client);
    
    Optional<Revenue> findByClientId(Long clientId);
}
