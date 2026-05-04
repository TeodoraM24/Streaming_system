package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.PaymentDTO;
import org.example.entities.Payment;
import org.example.entities.PaymentMethod;
import org.example.entities.Receipt;
import org.example.entities.Subscription;
import org.example.repositories.PaymentRepository;
import org.example.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
//@PreAuthorize("hasRole('ADMIN')") // all payment operations are admin-only
public class PaymentController {

    @Autowired private PaymentRepository repository;
    @Autowired private ReceiptRepository receiptRepository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<PaymentDTO> getAll() {
        return repository.findAll().stream().map(PaymentDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(PaymentDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO create(@RequestBody PaymentDTO dto) {
        Payment entity = new Payment(dto);
        if (dto.getSubscriptionId() != null) entity.setSubscription(entityManager.getReference(Subscription.class, dto.getSubscriptionId()));
        if (dto.getPaymentMethodId() != null) entity.setPaymentMethod(entityManager.getReference(PaymentMethod.class, dto.getPaymentMethodId()));
        Payment savedPayment = repository.save(entity);

        Receipt receipt = new Receipt();
        receipt.setPayment(savedPayment);
        receipt.setPrice(savedPayment.getPrice());
        receipt.setPaydate(LocalDateTime.now());
        receipt.setReceiptNumber(String.valueOf(System.currentTimeMillis()));
        receiptRepository.save(receipt);

        return PaymentDTO.convertToDTO(savedPayment);
    }

    @PatchMapping("/{id}")
    public PaymentDTO patch(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        Payment entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        return PaymentDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}