package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.Account;
import org.example.entities.Plan;
import org.example.entities.Subscription;
import org.example.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/{id}")
    public SubscriptionDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(SubscriptionDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDTO create(@RequestBody SubscriptionDTO dto) {
        Subscription entity = new Subscription(dto);
        if (dto.getAccountId() != null) entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        if (dto.getPlanId() != null) entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    public SubscriptionDTO update(@PathVariable Long id, @RequestBody SubscriptionDTO dto) {
        Subscription entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setStartdate(dto.getStartdate());
        entity.setEnddate(dto.getEnddate());
        entity.setNextBillDate(dto.getNextBillDate());
        entity.setStatus(dto.getStatus());
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}