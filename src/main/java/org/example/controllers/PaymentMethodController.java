package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.PaymentMethodDTO;
import org.example.entities.Account;
import org.example.entities.PaymentMethod;
import org.example.repositories.PaymentMethodRepository;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

    // USER: returns all payment methods belonging to the authenticated user's account
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<PaymentMethodDTO> getMyPaymentMethods(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getAccount()
                .getAccountId();
        return repository.findByAccount_AccountId(accountId)
                .stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    // ADMIN-only: listing all payment methods exposes financial data of all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentMethodDTO> getAll() {
        return repository.findAll().stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    // USER + owns or ADMIN: user may only view their own payment method
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @paymentMethodOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public PaymentMethodDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(PaymentMethodDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // USER: create a payment method tied to their own account
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public PaymentMethodDTO create(@RequestBody PaymentMethodDTO dto) {
        PaymentMethod entity = new PaymentMethod(dto);
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @paymentMethodOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public PaymentMethodDTO update(@PathVariable Long id, @RequestBody PaymentMethodDTO dto) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setCardNumber(dto.getCardNumber());
        entity.setExpirationMonth(dto.getExpirationMonth());
        entity.setExpirationYear(dto.getExpirationYear());
        entity.setCvc(dto.getCvc());
        entity.setType(dto.getType());
        entity.setDefaultPaymentmethod(dto.getDefaultPaymentmethod());
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @paymentMethodOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public PaymentMethodDTO patch(@PathVariable Long id, @RequestBody PaymentMethodDTO dto) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getCardNumber() != null) entity.setCardNumber(dto.getCardNumber());
        if (dto.getExpirationMonth() != null) entity.setExpirationMonth(dto.getExpirationMonth());
        if (dto.getExpirationYear() != null) entity.setExpirationYear(dto.getExpirationYear());
        if (dto.getCvc() != null) entity.setCvc(dto.getCvc());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getDefaultPaymentmethod() != null) entity.setDefaultPaymentmethod(dto.getDefaultPaymentmethod());
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @paymentMethodOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}