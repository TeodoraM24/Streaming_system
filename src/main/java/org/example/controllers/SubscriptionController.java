package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SubscriptionDTO;
import org.example.entities.Account;
import org.example.entities.Plan;
import org.example.entities.Subscription;
import org.example.enums.SubscriptionStatus;
import org.example.repositories.SubscriptionRepository;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired private SubscriptionRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubscriptionDTO> getAll() {
        return repository.findAll().stream().map(SubscriptionDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(SubscriptionDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public SubscriptionDTO create(@RequestBody SubscriptionDTO dto) {
        boolean hasActive = repository.findByAccount_AccountId(dto.getAccountId())
                .map(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .orElse(false);

        if (hasActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has active subscription");
        }

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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public SubscriptionDTO update(@PathVariable Long id, @RequestBody SubscriptionDTO dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Subscription entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifyOwnership(entity, userDetails);
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public SubscriptionDTO patch(@PathVariable Long id, @RequestBody SubscriptionDTO dto,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        Subscription entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifyOwnership(entity, userDetails);
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Subscription entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifyOwnership(entity, userDetails);
        repository.deleteById(id);
    }

    private void verifyOwnership(Subscription entity, UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Long accountId = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                    .getAccount().getAccountId();
            if (!entity.getAccount().getAccountId().equals(accountId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
    }
}