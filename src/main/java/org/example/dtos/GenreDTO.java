package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Genre;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreDTO {

    private Long genreId;
    private String genrename;

    public static GenreDTO convertToDTO(Genre genre) {
        if (genre == null) return null;
        return new GenreDTO(
                genre.getGenreId(),
                genre.getGenrename()
        );
    }
}