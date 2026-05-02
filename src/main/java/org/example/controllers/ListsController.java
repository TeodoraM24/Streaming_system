package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ListsDTO;
import org.example.entities.Content;
import org.example.entities.Lists;
import org.example.entities.Profile;
import org.example.repositories.ListsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lists")
public class ListsController {

    @Autowired private ListsRepository listsRepository;
    @Autowired private EntityManager entityManager;

    // ADMIN-only: listing all lists exposes other users' data
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ListsDTO> getAll() {
        return listsRepository.findAll().stream().map(ListsDTO::convertToDTO).toList();
    }

    // ADMIN-only: viewing any list by ID without ownership check
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ListsDTO getById(@PathVariable Long id) {
        return listsRepository.findById(id).map(ListsDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // USER: create a list — ownership is established via profileId in the DTO
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public ListsDTO create(@RequestBody ListsDTO dto) {
        Lists entity = new Lists(dto);
        if (dto.getProfileId() != null) {
            entity.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        setContents(entity, dto);
        return ListsDTO.convertToDTO(listsRepository.save(entity));
    }

    // USER + owns: user may only update their own list
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @listOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ListsDTO update(@PathVariable Long id, @RequestBody ListsDTO dto) {
        Lists entity = listsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setListname(dto.getListname());
        if (dto.getProfileId() != null) {
            entity.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        setContents(entity, dto);
        return ListsDTO.convertToDTO(listsRepository.save(entity));
    }

    // USER + owns: user may only patch their own list
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER') and @listOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ListsDTO patch(@PathVariable Long id, @RequestBody ListsDTO dto) {
        Lists entity = listsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getListname() != null) entity.setListname(dto.getListname());
        if (dto.getProfileId() != null) {
            entity.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        setContents(entity, dto);
        return ListsDTO.convertToDTO(listsRepository.save(entity));
    }

    // USER + owns: user may only delete their own list
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and @listOwnershipService.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        listsRepository.deleteById(id);
    }

    private void setContents(Lists entity, ListsDTO dto) {
        if (dto.getContentIds() != null) {
            List<Content> contents = dto.getContentIds().stream()
                    .map(id -> entityManager.getReference(Content.class, id))
                    .collect(Collectors.toCollection(ArrayList::new));
            entity.setContents(contents);
        }
    }
}