package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ShowDTO;
import org.example.entities.Content;
import org.example.entities.Show;
import org.example.repositories.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired private ShowRepository showRepository;
    @Autowired private EntityManager entityManager;

    @PostMapping
    public ShowDTO create(@RequestBody ShowDTO dto) {
        Show show = new Show(dto);
        if (dto.getContentId() != null) {
            show.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return ShowDTO.convertToDTO(showRepository.save(show));
    }
}