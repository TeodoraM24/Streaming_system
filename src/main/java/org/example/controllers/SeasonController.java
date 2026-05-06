package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SeasonDTO;
import org.example.entities.Season;
import org.example.entities.Show;
import org.example.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/seasons")
public class SeasonController {

    @Autowired private SeasonRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<SeasonDTO> getAll() {
        return repository.findAll().stream().map(SeasonDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public SeasonDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(SeasonDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonDTO create(@RequestBody SeasonDTO dto) {
        Season entity = new Season(dto);
        if (dto.getShowId() != null) {
            entity.setShow(entityManager.getReference(Show.class, dto.getShowId()));
        }
        return SeasonDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonDTO patch(@PathVariable Long id, @RequestBody SeasonDTO dto) {
        Season entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getReleasedate() != null) entity.setReleasedate(dto.getReleasedate());
        return SeasonDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}