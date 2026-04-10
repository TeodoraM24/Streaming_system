package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Movie;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {

    private Long movieId;
    private Short duration;
    private Long contentId;

    public static MovieDTO convertToDTO(Movie movie) {
        if (movie == null) return null;
        return new MovieDTO(
                movie.getMovieId(),
                movie.getDuration(),
                movie.getContent() != null ? movie.getContent().getContentId() : null
        );
    }
}