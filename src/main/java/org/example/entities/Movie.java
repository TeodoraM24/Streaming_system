package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.MovieDTO;

@Entity
@Table(name = "movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    private Short duration;

    @OneToOne(optional = false)
    @JoinColumn(name = "content_content_id", nullable = false)
    private Content content;

    public Movie(MovieDTO dto) {
        this.movieId = dto.getMovieId();
        this.duration = dto.getDuration();
    }
}