package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReceiptDTO;
import org.example.entities.Payment;
import org.example.entities.Receipt;
import org.example.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired private ReceiptRepository repository;
    @Autowired private EntityManager entityManager;

    @PostMapping
    public ReceiptDTO create(@RequestBody ReceiptDTO dto) {
        Receipt receipt = new Receipt(dto);
        if (dto.getPaymentId() != null) {
            receipt.setPayment(entityManager.getReference(Payment.class, dto.getPaymentId()));
        }
        return ReceiptDTO.convertToDTO(repository.save(receipt));
    }
}