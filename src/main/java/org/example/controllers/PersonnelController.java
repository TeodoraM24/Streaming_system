package org.example.controllers;

import org.example.dtos.PersonnelDTO;
import org.example.entities.Personnel;
import org.example.repositories.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/personnel")
public class PersonnelController {

    @Autowired private PersonnelRepository repository;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<PersonnelDTO> getAll() {
        return repository.findAll().stream().map(PersonnelDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public PersonnelDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(PersonnelDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonnelDTO create(@RequestBody PersonnelDTO dto) {
        return PersonnelDTO.convertToDTO(repository.save(new Personnel(dto)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PersonnelDTO patch(@PathVariable Long id, @RequestBody PersonnelDTO dto) {
        Personnel entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getRoletype() != null) entity.setRoletype(dto.getRoletype());
        return PersonnelDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}