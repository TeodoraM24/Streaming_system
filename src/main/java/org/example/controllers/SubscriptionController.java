package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.Account;
import org.example.entities.Plan;
import org.example.entities.Subscription;
import org.example.repositories.SubscriptionRepository;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired private SubscriptionRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

    // USER: returns the subscription belonging to the authenticated user's account
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public SubscriptionDTO getMySubscription(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getAccount()
                .getAccountId();
        return repository.findByAccount_AccountId(accountId)
                .map(SubscriptionDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active subscription found"));
    }

    // ADMIN-only: listing all subscriptions
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubscriptionDTO> getAll() {
        return repository.findAll().stream().map(SubscriptionDTO::convertToDTO).toList();
    }

    // ADMIN-only: viewing any subscription by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(SubscriptionDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // USER: subscribe — ownership is tied to accountId in DTO
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public SubscriptionDTO create(@RequestBody SubscriptionDTO dto) {
        Subscription entity = new Subscription(dto);
        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        if (dto.getPlanId() != null) {
            entity.setPlan(entityManager.getReference(Plan.class, dto.getPlanId()));
        }
        return SubscriptionDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @subscriptionOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
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

    // USER + owns or ADMIN
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @subscriptionOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
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

    // USER + owns or ADMIN
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @subscriptionOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}