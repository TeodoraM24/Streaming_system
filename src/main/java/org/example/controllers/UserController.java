package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.UserDTO;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        User user = new User(dto);
        if (dto.getAccountId() != null) {
            // This links the user to an existing account ID
            user.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return UserDTO.convertToDTO(userRepository.save(user));
    }
}