package org.example.controllers;

import org.example.dtos.UserDTO;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // ── Read ──────────────────────────────────────────────────────────────────

    @GetMapping
    public List<UserDTO> getAll() {
        return repository.findAll().stream().map(UserDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(UserDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * GET /users/me
     * Returns the currently authenticated user's own profile.
     * Use this on the frontend instead of /users/{id} for "my profile" views.
     */
    @GetMapping("/me")
    public UserDTO getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return repository.findByUsername(userDetails.getUsername())
                .map(UserDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // ── Update (own user only) ─────────────────────────────────────────────────

    /**
     * PATCH /users/me
     * Allows the authenticated user to update their own username.
     * Password changes should go through a dedicated /auth/change-password endpoint (not here).
     * No user can update another user's data.
     */
    @PatchMapping("/me")
    public UserDTO patchMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDTO dto
    ) {
        User existing = repository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getUsername() != null) {
            // Check the new username isn't already taken by someone else
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

    // ── Delete (own user only) ────────────────────────────────────────────────

    /**
     * DELETE /users/me
     * Deletes the authenticated user's own user record.
     * The linked Account is NOT deleted here — handle that via DELETE /accounts/me
     * if you want full account removal.
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal UserDetails userDetails) {
        User existing = repository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        repository.delete(existing);
    }

}