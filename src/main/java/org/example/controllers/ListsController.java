package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ListsDTO;
import org.example.entities.Content;
import org.example.entities.Lists;
import org.example.entities.Profile;
import org.example.repositories.ListsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<ListsDTO> getAll() {
        return listsRepository.findAll().stream().map(ListsDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public ListsDTO getById(@PathVariable Long id) {
        return listsRepository.findById(id).map(ListsDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListsDTO create(@RequestBody ListsDTO dto) {
        Lists entity = new Lists(dto);
        if (dto.getProfileId() != null) {
            entity.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        setContents(entity, dto);
        return ListsDTO.convertToDTO(listsRepository.save(entity));
    }

    @PutMapping("/{id}")
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

    @PatchMapping("/{id}")
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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