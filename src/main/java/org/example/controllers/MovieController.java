package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.MovieDTO;
import org.example.dtos.MovieResponseDTO;
import org.example.entities.Content;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<MovieDTO> getAll() {
        return movieRepository.findAll().stream().map(MovieDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    public MovieDTO getById(@PathVariable Long id) {
        return movieRepository.findById(id).map(MovieDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @PatchMapping("/{id}")
    public MovieDTO patch(@PathVariable Long id, @RequestBody MovieDTO dto) {
        Movie entity = movieRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.getDuration() != null) entity.setDuration(dto.getDuration());
        if (dto.getContentId() != null) {
            entity.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return MovieDTO.convertToDTO(movieRepository.save(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    @GetMapping("/top-movies")
    public List<MovieResponseDTO> getTopRatedMovies() {
        return movieService.getTop10RatedMovies();
    }
}