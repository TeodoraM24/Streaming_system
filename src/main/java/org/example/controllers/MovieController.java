package org.example.controllers;

import jakarta.persistence.EntityManager;
import org.example.dtos.MovieDTO;
import org.example.dtos.MovieResponseDTO;
import org.example.entities.Content;
import org.example.entities.Movie;
import org.example.repositories.GenreRepository;
import org.example.repositories.MovieRepository;
import org.example.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    private GenreRepository genreRepository;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<MovieDTO> getAll() {
        return movieRepository.findAll().stream().map(MovieDTO::convertToDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public MovieDTO getById(@PathVariable Long id) {
        return movieRepository.findById(id).map(MovieDTO::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    @GetMapping("/top-movies")
    public List<MovieResponseDTO> getTopRatedMovies() {
        return movieService.getTop10RatedMovies();
    }


    @GetMapping("/genre/{genreId}")
    @PreAuthorize("hasRole('USER')")
    public List<MovieDTO> getMoviesByGenre(@PathVariable Long genreId) {
        // Validate that the genre actually exists — otherwise 404
        if (!genreRepository.existsById(genreId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }

        List<MovieDTO> movies = movieRepository.findByContent_Genres_GenreId(genreId)
                .stream()
                .map(MovieDTO::convertToDTO)
                .toList();

        return movies; // Empty list is a valid 200 — caller shows "no movies found" message
    }
}