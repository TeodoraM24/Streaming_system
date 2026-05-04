package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.ShowDTO;
import org.example.dtos.ShowResponseDTO;
import org.example.entities.Content;
import org.example.entities.Show;
import org.example.repositories.GenreRepository;
import org.example.repositories.ShowRepository;
import org.example.services.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired private ShowRepository repository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private ShowService showService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<ShowDTO> getAll() {
        return repository.findAll().stream().map(ShowDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ShowDTO getById(@PathVariable Long id) {
        return repository.findById(id).map(ShowDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/genre/{genreId}")
    @PreAuthorize("hasRole('USER')")
    public List<ShowDTO> getShowsByGenre(@PathVariable Long genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }

        return repository.findByContent_Genres_GenreId(genreId)
                .stream()
                .map(ShowDTO::convertToDTO)
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ShowDTO update(@PathVariable Long id, @RequestBody ShowDTO dto) {
        Show entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getContentId() != null) {
            entity.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return ShowDTO.convertToDTO(repository.save(entity));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/top-10-shows")
    public List<ShowResponseDTO> getTopRatedShows() {
        return showService.getTop10RatedShows();
    }
}
