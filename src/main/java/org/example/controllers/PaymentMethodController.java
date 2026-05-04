package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.PaymentMethodDTO;
import org.example.entities.Account;
import org.example.entities.PaymentMethod;
import org.example.repositories.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<PaymentMethodDTO> getAll() {
        return repository.findAll().stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public PaymentMethodDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(PaymentMethodDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDTO create(@Valid @RequestBody PaymentMethodDTO dto) {
        PaymentMethod entity = new PaymentMethod(dto);
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    public PaymentMethodDTO update(@PathVariable Long id, @Valid @RequestBody PaymentMethodDTO dto) {
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

    @PatchMapping("/{id}")
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}