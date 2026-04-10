package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.EpisodeDTO;
import org.example.entities.Episode;
import org.example.entities.Season;
import org.example.repositories.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/episodes")
public class EpisodeController {
    @Autowired private EpisodeRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<EpisodeDTO> getAll() {
        return repository.findAll().stream().map(EpisodeDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public EpisodeDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(EpisodeDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpisodeDTO create(@RequestBody EpisodeDTO dto) {
        Episode entity = new Episode(dto);
        if (dto.getSeasonId() != null) {
            entity.setSeason(entityManager.getReference(Season.class, dto.getSeasonId()));
        }
        return EpisodeDTO.convertToDTO(repository.save(entity));
    }

    @PutMapping("/{id}")
    public EpisodeDTO update(@PathVariable Long id, @RequestBody EpisodeDTO dto) {
        Episode entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReleasedate(dto.getReleasedate());
        entity.setDuration(dto.getDuration());
        return EpisodeDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}