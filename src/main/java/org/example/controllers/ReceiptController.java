package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ReceiptDTO;
import org.example.entities.Payment;
import org.example.entities.Receipt;
import org.example.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired private ReceiptRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<ReceiptDTO> getAll() {
        return repository.findAll().stream().map(ReceiptDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public ReceiptDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ReceiptDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptDTO create(@RequestBody ReceiptDTO dto) {
        Receipt entity = new Receipt(dto);
        if (dto.getPaymentId() != null) {
            entity.setPayment(entityManager.getReference(Payment.class, dto.getPaymentId()));
        }
        return ReceiptDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
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
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}