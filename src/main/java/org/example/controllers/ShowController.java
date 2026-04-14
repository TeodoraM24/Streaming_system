package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ShowDTO;
import org.example.entities.Content;
import org.example.entities.Show;
import org.example.repositories.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired private ShowRepository repository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<ShowDTO> getAll() {
        return repository.findAll().stream().map(ShowDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public ShowDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ShowDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ShowDTO update(@PathVariable Long id, @RequestBody ShowDTO dto) {
        Show entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getContentId() != null) {
            entity.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return ShowDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    public ShowDTO patch(@PathVariable Long id, @RequestBody ShowDTO dto) {
        Show entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getContentId() != null) {
            entity.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return ShowDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}