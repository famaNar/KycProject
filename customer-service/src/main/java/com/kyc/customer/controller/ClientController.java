package com.kyc.customer.controller;

import com.kyc.customer.dto.ClientRegistrationRequest;
import com.kyc.customer.dto.ClientResponse;
import com.kyc.customer.dto.KycFormRequest;
import com.kyc.customer.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/list")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/list/email/{email}")
    public ResponseEntity<ClientResponse> getClientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(clientService.getClientByEmail(email));
    }

    @PostMapping("/register")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        return new ResponseEntity<>(clientService.registerClient(request), HttpStatus.CREATED);
    }

    @PostMapping("/kyc/submit")
    public ResponseEntity<ClientResponse> submitKycForm(@Valid @RequestBody KycFormRequest request) {
        return ResponseEntity.ok(clientService.submitKycForm(request));
    }

    @PutMapping("/update/{id}/kyc/approve")
    public ResponseEntity<ClientResponse> approveKyc(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.approveKyc(id));
    }

    @PutMapping("/update/{id}/kyc/reject")
    public ResponseEntity<ClientResponse> rejectKyc(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(clientService.rejectKyc(id, reason));
    }
}
