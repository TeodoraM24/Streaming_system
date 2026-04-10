package org.example.controllers;

import org.example.dtos.AccountDTO;
import org.example.entities.Account;
import org.example.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public List<AccountDTO> getAll() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccountDTO getById(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"
                ));

        return AccountDTO.convertToDTO(account);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO create(@RequestBody AccountDTO dto) {
        Account account = new Account(dto);
        return AccountDTO.convertToDTO(accountRepository.save(account));
    }

    @PutMapping("/{id}")
    public AccountDTO update(@PathVariable Long id, @RequestBody AccountDTO dto) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"
                ));

        // overwrite fields
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());
        existing.setPhonenumber(dto.getPhonenumber());
        existing.setMail(dto.getMail());

        return AccountDTO.convertToDTO(accountRepository.save(existing));
    }

    @PatchMapping("/{id}")
    public AccountDTO patch(@PathVariable Long id, @RequestBody AccountDTO dto) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"
                ));

        // only update non-null fields
        if (dto.getFirstname() != null) {
            existing.setFirstname(dto.getFirstname());
        }
        if (dto.getLastname() != null) {
            existing.setLastname(dto.getLastname());
        }
        if (dto.getPhonenumber() != null) {
            existing.setPhonenumber(dto.getPhonenumber());
        }
        if (dto.getMail() != null) {
            existing.setMail(dto.getMail());
        }

        return AccountDTO.convertToDTO(accountRepository.save(existing));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"
                ));

        accountRepository.delete(existing);
    }
}