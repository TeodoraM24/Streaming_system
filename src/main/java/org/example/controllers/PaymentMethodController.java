package org.example.controllers;

import org.example.dtos.PaymentMethodDTO;
import org.example.entities.PaymentMethod;
import org.example.repositories.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {
    @Autowired private PaymentMethodRepository repository;

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
    public PaymentMethodDTO create(@RequestBody PaymentMethodDTO dto) {
        return PaymentMethodDTO.convertToDTO(repository.save(new PaymentMethod(dto)));
    }

    @PutMapping("/{id}")
    public PaymentMethodDTO update(@PathVariable Long id, @RequestBody PaymentMethodDTO dto) {
        PaymentMethod entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setCardNumber(dto.getCardNumber());
        entity.setExpirationMonth(dto.getExpirationMonth());
        entity.setExpirationYear(dto.getExpirationYear());
        entity.setCvc(dto.getCvc());
        entity.setType(dto.getType());
        entity.setDefaultPaymentmethod(dto.getDefaultPaymentmethod());
        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}