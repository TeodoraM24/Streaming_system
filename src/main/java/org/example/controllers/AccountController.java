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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO create(@RequestBody AccountDTO dto) {
        Account account = new Account(dto);
        return AccountDTO.convertToDTO(accountRepository.save(account));
    }
}