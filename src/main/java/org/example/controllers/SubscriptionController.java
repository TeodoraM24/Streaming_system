package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.Account;
import org.example.entities.Plan;
import org.example.entities.Subscription;
import org.example.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired private SubscriptionRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<SubscriptionDTO> getAll() {
        return repository.findAll().stream().map(SubscriptionDTO::convertToDTO).toList();
    }

    @PostMapping
    public SubscriptionDTO create(@RequestBody SubscriptionDTO dto) {
        Subscription entity = new Subscription(dto);
        // Link the FKs
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        if (dto.getPlanId() != null) {
            entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));
        }
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }
}