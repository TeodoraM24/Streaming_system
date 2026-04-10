package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ListsDTO;
import org.example.entities.Lists;
import org.example.entities.Profile;
import org.example.repositories.ListsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/lists")
public class ListsController {

    @Autowired private ListsRepository listsRepository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<ListsDTO> getAll() {
        return listsRepository.findAll().stream().map(ListsDTO::convertToDTO).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListsDTO create(@RequestBody ListsDTO dto) {
        Lists list = new Lists(dto);
        if (dto.getProfileId() != null) {
            list.setProfile(entityManager.getReference(Profile.class, dto.getProfileId()));
        }
        return ListsDTO.convertToDTO(listsRepository.save(list));
    }

    @PatchMapping("/{id}")
    public ListsDTO patch(@PathVariable Long id, @RequestBody ListsDTO dto) {
        Lists entity = listsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getListname() != null) entity.setListname(dto.getListname());
        return ListsDTO.convertToDTO(listsRepository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        listsRepository.deleteById(id);
    }
}