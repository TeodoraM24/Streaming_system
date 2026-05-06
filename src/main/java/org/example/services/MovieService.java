package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.MovieResponseDTO;
import org.example.entities.Content;
import org.example.repositories.MovieRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieResponseDTO> getTop10RatedMovies() {

        return movieRepository.findTop10ByOrderByContent_RatingDesc()
                .stream()
                .map(m -> {
                    Content c = m.getContent();

                    MovieResponseDTO dto = new MovieResponseDTO();
                    dto.setMovieId(m.getMovieId());
                    dto.setDuration(m.getDuration());

                    dto.setTitle(c.getTitle());
                    dto.setRating(c.getRating());
                    dto.setDescription(c.getDescription());

                    return dto;
                })
                .toList();
    }
}