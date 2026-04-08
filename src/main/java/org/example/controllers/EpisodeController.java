package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.EpisodeDTO;
import org.example.entities.Episode;
import org.example.entities.Season;
import org.example.repositories.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public EpisodeDTO create(@RequestBody EpisodeDTO dto) {
        Episode entity = new Episode(dto);
        if (dto.getSeasonId() != null) {
            entity.setSeason(entityManager.getReference(Season.class, dto.getSeasonId()));
        }
        return EpisodeDTO.convertToDTO(repository.save(entity));
    }
}