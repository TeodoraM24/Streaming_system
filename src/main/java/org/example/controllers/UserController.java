package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.UserDTO;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired private UserRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<UserDTO> getAll() {
        return repository.findAll().stream().map(UserDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(UserDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserDTO dto) {
        User user = new User(dto);
        if (dto.getAccountId() != null) {
            user.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return UserDTO.convertToDTO(repository.save(user));
    }


    //update will be added once security is set up

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}