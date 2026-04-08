package org.example.controllers;

import org.example.dtos.GenreDTO;
import org.example.entities.Genre;
import org.example.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired private GenreRepository repository;

    @GetMapping
    public List<GenreDTO> getAll() {
        return repository.findAll().stream().map(GenreDTO::convertToDTO).toList();
    }

    @PostMapping
    public GenreDTO create(@RequestBody GenreDTO dto) {
        return GenreDTO.convertToDTO(repository.save(new Genre(dto)));
    }
}