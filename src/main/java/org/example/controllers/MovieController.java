package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.MovieDTO;
import org.example.entities.Content;
import org.example.entities.Movie;
import org.example.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired private MovieRepository movieRepository;
    @Autowired private EntityManager entityManager;

    @GetMapping
    public List<MovieDTO> getAll() {
        return movieRepository.findAll().stream().map(MovieDTO::convertToDTO).toList();
    }

    @PostMapping
    public MovieDTO create(@RequestBody MovieDTO dto) {
        Movie movie = new Movie(dto);
        if (dto.getContentId() != null) {
            movie.setContent(entityManager.getReference(Content.class, dto.getContentId()));
        }
        return MovieDTO.convertToDTO(movieRepository.save(movie));
    }
}