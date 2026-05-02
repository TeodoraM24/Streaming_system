package org.example.controllers;

import org.example.dtos.AccountDTO;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.repositories.AccountRepository;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired private AccountRepository accountRepository;
    @Autowired private UserRepository userRepository;

    /**
     * Resolves the account by ID and verifies the calling user owns it.
     * Throws 403 if the account belongs to someone else.
     */
    private Account resolveOwnedAccount(Long accountId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (user.getAccount() == null || !user.getAccount().getAccountId().equals(accountId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this account");
        return account;
    }

    /**
     * GET /accounts — list all accounts, admin only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDTO> getAll() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET /accounts/{id} — get any account by ID, admin only.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountDTO getById(@PathVariable Long id) {
        return accountRepository.findById(id).map(AccountDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    /**
     * GET /accounts/me — authenticated user's own account.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AccountDTO getMe(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (user.getAccount() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account linked to this user");
        return AccountDTO.convertToDTO(user.getAccount());
    }

    /**
     * PUT /accounts/{id} — full update, ownership enforced.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AccountDTO update(@PathVariable Long id, @RequestBody AccountDTO dto,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Account existing = resolveOwnedAccount(id, userDetails);
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());
        existing.setPhonenumber(dto.getPhonenumber());
        existing.setMail(dto.getMail());
        return AccountDTO.convertToDTO(accountRepository.save(existing));
    }

    /**
     * PATCH /accounts/{id} — partial update, ownership enforced.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AccountDTO patch(@PathVariable Long id, @RequestBody AccountDTO dto,
                            @AuthenticationPrincipal UserDetails userDetails) {
        Account existing = resolveOwnedAccount(id, userDetails);
        if (dto.getFirstname() != null)   existing.setFirstname(dto.getFirstname());
        if (dto.getLastname() != null)    existing.setLastname(dto.getLastname());
        if (dto.getPhonenumber() != null) existing.setPhonenumber(dto.getPhonenumber());
        if (dto.getMail() != null)        existing.setMail(dto.getMail());
        return AccountDTO.convertToDTO(accountRepository.save(existing));
    }


}