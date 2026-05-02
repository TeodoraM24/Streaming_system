package org.example.controllers;

import org.example.dtos.UserDTO;
import org.example.entities.User;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repository;

    /**
     * GET /users — all users, admin only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAll() {
        return repository.findAll().stream().map(UserDTO::convertToDTO).toList();
    }

    /**
     * GET /users/{id} — look up any user by ID, admin only.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(UserDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * GET /users/me — authenticated user's own profile.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDTO getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return repository.findByUsername(userDetails.getUsername())
                .map(UserDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * PATCH /users/me — update own username. Password changes go through /auth/change-password.
     */
    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDTO patchMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDTO dto
    ) {
        User existing = repository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getUsername() != null) {
            boolean taken = repository.findByUsername(dto.getUsername())
                    .filter(u -> !u.getUsersId().equals(existing.getUsersId()))
                    .isPresent();
            if (taken) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
            }
            existing.setUsername(dto.getUsername());
        }

        return UserDTO.convertToDTO(repository.save(existing));
    }

    /**
     * DELETE /users/me — delete own user record.
     * Account is not deleted here — use DELETE /accounts/me for full removal.
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void deleteMe(@AuthenticationPrincipal UserDetails userDetails) {
        User existing = repository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        repository.delete(existing);
    }
}