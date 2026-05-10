package org.example.repositories;
import org.example.entities.Show;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByContent_Genres_GenreId(Long genreId);
    List<Show> findTop10ByOrderByContent_RatingDesc();

    @EntityGraph(attributePaths = {"content", "seasons"})
    Optional<Show> findWithSeasonsByShowsId(Long showsId);
}
