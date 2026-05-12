package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.PaymentMethodDTO;
import org.example.entities.Account;
import org.example.entities.PaymentMethod;
import org.example.repositories.PaymentMethodRepository;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.example.services.PaymentMethodValidation;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private PaymentMethodValidation paymentMethodValidation;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<PaymentMethodDTO> getMyPaymentMethods(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getAccount()
                .getAccountId();
        return repository.findByAccount_AccountId(accountId)
                .stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentMethodDTO> getAll() {
        return repository.findAll().stream().map(PaymentMethodDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PaymentMethodDTO getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifyOwnership(entity, userDetails);
        return PaymentMethodDTO.convertToDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public PaymentMethodDTO create(@RequestBody PaymentMethodDTO dto) {
        paymentMethodValidation.validateCardNumber(dto.getCardNumber());
        paymentMethodValidation.validateExpirationMonth(dto.getExpirationMonth());
        paymentMethodValidation.validateExpirationYear(dto.getExpirationYear());
        paymentMethodValidation.validateCvc(dto.getCvc());
        paymentMethodValidation.validateType(dto.getType());
        paymentMethodValidation.validateAccountId(dto.getAccountId());

        PaymentMethod entity = new PaymentMethod(dto);

        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }

        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PaymentMethodDTO update(@PathVariable Long id, @RequestBody PaymentMethodDTO dto,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        verifyOwnership(entity, userDetails);

        paymentMethodValidation.validateCardNumber(dto.getCardNumber());
        paymentMethodValidation.validateExpirationMonth(dto.getExpirationMonth());
        paymentMethodValidation.validateExpirationYear(dto.getExpirationYear());
        paymentMethodValidation.validateCvc(dto.getCvc());
        paymentMethodValidation.validateType(dto.getType());

        if (dto.getAccountId() != null) {
            paymentMethodValidation.validateAccountId(dto.getAccountId());
        }

        entity.setCardNumber(dto.getCardNumber());
        entity.setExpirationMonth(dto.getExpirationMonth());
        entity.setExpirationYear(dto.getExpirationYear());
        entity.setCvc(dto.getCvc());
        entity.setType(dto.getType());
        entity.setDefaultPaymentmethod(dto.getDefaultPaymentmethod());

        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }

        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PaymentMethodDTO patch(@PathVariable Long id, @RequestBody PaymentMethodDTO dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        verifyOwnership(entity, userDetails);

        if (dto.getCardNumber() != null) {
            paymentMethodValidation.validateCardNumber(dto.getCardNumber());
        }

        if (dto.getExpirationMonth() != null) {
            paymentMethodValidation.validateExpirationMonth(dto.getExpirationMonth());
        }

        if (dto.getExpirationYear() != null) {
            paymentMethodValidation.validateExpirationYear(dto.getExpirationYear());
        }

        if (dto.getCvc() != null) {
            paymentMethodValidation.validateCvc(dto.getCvc());
        }

        if (dto.getType() != null) {
            paymentMethodValidation.validateType(dto.getType());
        }

        if (dto.getAccountId() != null) {
            paymentMethodValidation.validateAccountId(dto.getAccountId());
        }

        if (dto.getCardNumber() != null) entity.setCardNumber(dto.getCardNumber());
        if (dto.getExpirationMonth() != null) entity.setExpirationMonth(dto.getExpirationMonth());
        if (dto.getExpirationYear() != null) entity.setExpirationYear(dto.getExpirationYear());
        if (dto.getCvc() != null) entity.setCvc(dto.getCvc());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getDefaultPaymentmethod() != null) entity.setDefaultPaymentmethod(dto.getDefaultPaymentmethod());

        if (dto.getAccountId() != null) {
            entity.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }

        return PaymentMethodDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifyOwnership(entity, userDetails);
        repository.deleteById(id);
    }

    private void verifyOwnership(PaymentMethod entity, UserDetails userDetails) {
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