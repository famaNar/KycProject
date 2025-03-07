package com.kyc.customer.controller;

import com.kyc.customer.entity.Revenue;
import com.kyc.customer.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/revenues")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Revenue>> getRevenuesByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(revenueService.getRevenuesByClientId(clientId));
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<Revenue> saveRevenue(
            @PathVariable Long clientId,
            @RequestParam BigDecimal revenuAnnuel,
            @RequestParam String sourceRevenu,
            @RequestParam(required = false) String autresSources,
            @RequestParam(required = false) BigDecimal patrimoineEstime) {
        
        Revenue savedRevenue = revenueService.saveRevenue(
                clientId, revenuAnnuel, sourceRevenu, autresSources, patrimoineEstime);
        
        return ResponseEntity.ok(savedRevenue);
    }

    @GetMapping("/client/{clientId}/details")
    public ResponseEntity<Revenue> getRevenueByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(revenueService.getRevenueByClientId(clientId));
    }
}
