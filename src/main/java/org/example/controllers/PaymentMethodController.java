package org.example.controllers;

import org.example.dtos.PaymentMethodDTO;
import org.example.entities.PaymentMethod;
import org.example.repositories.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodRepository repository;

    @GetMapping
    public List<PaymentMethodDTO> getAll() {
        return repository.findAll().stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    @PostMapping
    public PaymentMethodDTO create(@RequestBody PaymentMethodDTO dto) {
        return PaymentMethodDTO.convertToDTO(repository.save(new PaymentMethod(dto)));
    }
}