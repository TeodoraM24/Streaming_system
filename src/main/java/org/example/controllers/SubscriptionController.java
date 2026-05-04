package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.Account;
import org.example.entities.Plan;
import org.example.entities.Subscription;
import org.example.enums.SubscriptionStatus;
import org.example.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDTO create(@RequestBody SubscriptionDTO dto) {

        // 👉 HER
        boolean hasActive = repository.findAll().stream()
                .anyMatch(s -> s.getAccount().getAccountId().equals(dto.getAccountId())
                        && s.getStatus() == SubscriptionStatus.ACTIVE);

        if (hasActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has active subscription");
        }

        // 👉 Derefter din normale kode
        Subscription entity = new Subscription();

        entity.setStartdate(LocalDate.now());
        entity.setEnddate(LocalDate.now().plusMonths(1));
        entity.setNextBillDate(LocalDate.now().plusMonths(1));
        entity.setStatus(SubscriptionStatus.ACTIVE);

        entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));

        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    public SubscriptionDTO update(@PathVariable Long id, @RequestBody SubscriptionDTO dto) {
        Subscription entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setStartdate(dto.getStartdate());
        entity.setEnddate(dto.getEnddate());
        entity.setNextBillDate(dto.getNextBillDate());
        entity.setStatus(dto.getStatus());
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        if (dto.getPlanId() != null) {
            entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));
        }
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    public SubscriptionDTO patch(@PathVariable Long id, @RequestBody SubscriptionDTO dto) {
        Subscription entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getStartdate() != null) entity.setStartdate(dto.getStartdate());
        if (dto.getEnddate() != null) entity.setEnddate(dto.getEnddate());
        if (dto.getNextBillDate() != null) entity.setNextBillDate(dto.getNextBillDate());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        if (dto.getPlanId() != null) {
            entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));
        }
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}