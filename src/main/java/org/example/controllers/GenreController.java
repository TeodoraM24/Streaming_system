package org.example.controllers;

import org.example.dtos.GenreDTO;
import org.example.entities.Genre;
import org.example.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired private GenreRepository repository;

    @GetMapping
    public List<GenreDTO> getAll() {
        return repository.findAll().stream().map(GenreDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public GenreDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(GenreDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDTO create(@RequestBody GenreDTO dto) {
        return GenreDTO.convertToDTO(repository.save(new Genre(dto)));
    }

    @PutMapping("/{id}")
    public GenreDTO update(@PathVariable Long id, @RequestBody GenreDTO dto) {
        Genre entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setGenrename(dto.getGenrename());
        return GenreDTO.convertToDTO(repository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repository.deleteById(id);
    }
}