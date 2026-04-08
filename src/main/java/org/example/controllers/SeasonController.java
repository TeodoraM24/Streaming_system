package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.SeasonDTO;
import org.example.entities.Season;
import org.example.entities.Show;
import org.example.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seasons")
public class SeasonController {

    @Autowired private SeasonRepository repository;
    @Autowired private EntityManager entityManager;

    @PostMapping
    public SeasonDTO create(@RequestBody SeasonDTO dto) {
        Season entity = new Season(dto);
        if (dto.getShowId() != null) {
            // Note: Use getShowsId() in the Entity to match your SQL 'shows_id'
            entity.setShow(entityManager.getReference(Show.class, dto.getShowId()));
        }
        return SeasonDTO.convertToDTO(repository.save(entity));
    }
}