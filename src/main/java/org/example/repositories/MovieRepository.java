package org.example.repositories;
import org.example.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findTop10ByOrderByContent_RatingDesc();
    List<Movie> findByContent_Genres_GenreId(Long genreId);
}


