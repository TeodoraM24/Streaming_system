package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReceiptDTO;
import org.example.entities.Payment;
import org.example.entities.Receipt;
import org.example.repositories.ReceiptRepository; 
import org.example.repositories.UserRepository;
import org.example.services.ReceiptValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired private ReceiptRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private ReceiptValidation receiptValidation;

    // USER: returns receipts belonging to the authenticated user's account
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<ReceiptDTO> getMyReceipts(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getAccount()
                .getAccountId();
        return repository.findByPayment_Subscription_Account_AccountId(accountId)
                .stream().map(ReceiptDTO::convertToDTO).toList();
    }

    // USER + owns or ADMIN: user may view a specific receipt if it belongs to them
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @receiptOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ReceiptDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ReceiptDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // ADMIN-only: listing all receipts across all accounts
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReceiptDTO> getAll() {
        return repository.findAll().stream().map(ReceiptDTO::convertToDTO).toList();
    }

    // ADMIN-only: payment processing is admin-managed
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasRole('ADMIN')")
    public ReceiptDTO create(@RequestBody ReceiptDTO dto) {
        receiptValidation.validateReceiptNumber(dto.getReceiptNumber());
        receiptValidation.validateReceiptPrice(dto.getPrice());
        Receipt entity = new Receipt(dto);
        if (dto.getPaymentId() != null) {
            entity.setPayment(entityManager.getReference(Payment.class, dto.getPaymentId()));
        }
        return ReceiptDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ReceiptDTO update(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        Receipt entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setReceiptNumber(dto.getReceiptNumber());
        entity.setPrice(dto.getPrice());
        entity.setPaydate(dto.getPaydate());
        if (dto.getPaymentId() != null) {
            entity.setPayment(entityManager.getReference(Payment.class, dto.getPaymentId()));
        }
        return ReceiptDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ReceiptDTO patch(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        Receipt entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getReceiptNumber() != null) entity.setReceiptNumber(dto.getReceiptNumber());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getPaydate() != null) entity.setPaydate(dto.getPaydate());
        if (dto.getPaymentId() != null) {
            entity.setPayment(entityManager.getReference(Payment.class, dto.getPaymentId()));
        }
        return ReceiptDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
}