package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ProfileDTO;
import org.example.entities.Account;
import org.example.entities.Profile;
import org.example.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired private ProfileRepository profileRepository;
    @Autowired private EntityManager entityManager;

    @PostMapping
    public ProfileDTO create(@RequestBody ProfileDTO dto) {
        Profile profile = new Profile(dto);
        if (dto.getAccountId() != null) {
            profile.setAccount(entityManager.getReference(Account.class, dto.getAccountId()));
        }
        return ProfileDTO.convertToDTO(profileRepository.save(profile));
    }
}