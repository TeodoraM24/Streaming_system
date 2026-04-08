package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.PaymentDTO;
import org.example.entities.Payment;
import org.example.entities.PaymentMethod;
import org.example.entities.Subscription;
import org.example.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private PaymentRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<PaymentDTO> getAll() {
        return repository.findAll().stream().map(PaymentDTO::convertToDTO).toList();
    }

    @PostMapping
    public PaymentDTO create(@RequestBody PaymentDTO dto) {
        Payment entity = new Payment(dto);
        // Link the FKs
        if (dto.getSubscriptionId() != null) {
            entity.setSubscription(entityManager.getReference(Subscription.class, dto.getSubscriptionId()));
        }
        if (dto.getPaymentMethodId() != null) {
            entity.setPaymentMethod(entityManager.getReference(PaymentMethod.class, dto.getPaymentMethodId()));
        }
        return PaymentDTO.convertToDTO(repository.save(entity));
    }
}