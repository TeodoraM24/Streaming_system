package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ProfileDTO;
import org.example.entities.Account;
import org.example.entities.Profile;
import org.example.repositories.ProfileRepository;
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
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired private ProfileRepository repository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

    // USER: returns all profiles belonging to the authenticated user's account
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<ProfileDTO> getMyProfiles(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getAccount()
                .getAccountId();
        return repository.findByAccount_AccountId(accountId)
                .stream().map(ProfileDTO::convertToDTO).toList();
    }

    // ADMIN-only: listing all profiles across all accounts
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProfileDTO> getAll() {
        return repository.findAll().stream().map(ProfileDTO::convertToDTO).toList();
    }

    // ADMIN-only: viewing any profile by ID without ownership context
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProfileDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ProfileDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // USER: create a profile under their own account
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ProfileDTO create(@RequestBody ProfileDTO dto) {
        Profile profile = new Profile(dto);
        if (dto.getAccountId() != null) {
            profile.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return ProfileDTO.convertToDTO(repository.save(profile));
    }

    // USER + owns or ADMIN
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @profileOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ProfileDTO patch(@PathVariable Long id, @RequestBody ProfileDTO dto) {
        Profile entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getProfilename() != null) entity.setProfilename(dto.getProfilename());
        return ProfileDTO.convertToDTO(repository.save(entity));
    }

    // USER + owns or ADMIN
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @profileOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}