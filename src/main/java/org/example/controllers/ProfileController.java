package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ProfileDTO;
import org.example.entities.Account;
import org.example.entities.Profile;
import org.example.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired private ProfileRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<ProfileDTO> getAll() {
        return repository.findAll().stream().map(ProfileDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public ProfileDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ProfileDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDTO create(@RequestBody ProfileDTO dto) {
        Profile profile = new Profile(dto);
        if (dto.getAccountId() != null) {
            profile.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return ProfileDTO.convertToDTO(repository.save(profile));
    }

    @PatchMapping("/{id}")
    public ProfileDTO patch(@PathVariable Long id, @RequestBody ProfileDTO dto) {
        Profile entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getProfilename() != null) entity.setProfilename(dto.getProfilename());
        return ProfileDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}