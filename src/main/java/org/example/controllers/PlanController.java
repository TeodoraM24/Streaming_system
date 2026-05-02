package org.example.controllers;

import org.example.dtos.PlanDTO;
import org.example.entities.Plan;
import org.example.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired private PlanRepository repository;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<PlanDTO> getAll() {
        return repository.findAll().stream().map(PlanDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public PlanDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(PlanDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PlanDTO create(@RequestBody PlanDTO dto) {
        return PlanDTO.convertToDTO(repository.save(new Plan(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanDTO update(@PathVariable Long id, @RequestBody PlanDTO dto) {
        Plan entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setCurrency(dto.getCurrency());
        entity.setActive(dto.getActive());
        return PlanDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}