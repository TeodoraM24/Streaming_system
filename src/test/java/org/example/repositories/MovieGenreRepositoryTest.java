package org.example.repositories;

import org.example.entities.Content;
import org.example.entities.Genre;
import org.example.entities.Movie;
import org.example.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class MovieGenreRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("postgres/init_types.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        // Clear in the correct order to respect FK constraints:
        // Movies reference Content, Content join table references Genre
        movieRepository.deleteAll();
        contentRepository.deleteAll();
        genreRepository.deleteAll();
    }

    // Helper: creates and saves a Genre with the given name
    private Genre createAndSaveGenre(String name) {
        Genre genre = new Genre();
        genre.setGenrename(name);
        return genreRepository.save(genre);
    }

    // Helper: creates a Content linked to the given genre, then wraps it in a Movie
    private Movie createAndSaveMovieWithGenre(String title, Genre genre) {
        Content content = new Content();
        content.setOriginaltitle(title);
        content.setTitle(title);
        content.setRating(BigDecimal.valueOf(7.0));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.MOVIE);
        // Assign the genre to the Content (Content owns the ManyToMany join table)
        content.setGenres(List.of(genre));

        Content savedContent = contentRepository.save(content);

        Movie movie = new Movie();
        movie.setDuration((short) 120);
        movie.setContent(savedContent);

        return movieRepository.save(movie);
    }

    @Test
    void findByContent_Genres_GenreId_returnsMoviesForThatGenre() {
        // Arrange: create a genre and link 2 movies to it
        Genre action = createAndSaveGenre("Action");
        createAndSaveMovieWithGenre("Die Hard", action);
        createAndSaveMovieWithGenre("Mad Max", action);

        // Act: query movies by the action genre's ID
        List<Movie> result = movieRepository.findByContent_Genres_GenreId(action.getGenreId());

        // Assert: both movies should be returned
        assertEquals(2, result.size(), "Should return all movies linked to the genre");
    }

    @Test
    void findByContent_Genres_GenreId_returnsOnlyMoviesFromThatGenre() {
        // Arrange: two genres, each with their own movie
        Genre action = createAndSaveGenre("Action");
        Genre comedy = createAndSaveGenre("Comedy");
        Movie actionMovie = createAndSaveMovieWithGenre("Die Hard", action);
        createAndSaveMovieWithGenre("Dumb and Dumber", comedy);

        // Act: query for action movies only
        List<Movie> result = movieRepository.findByContent_Genres_GenreId(action.getGenreId());

        // Assert: only the action movie is returned — no cross-genre contamination
        assertEquals(1, result.size(), "Should return only movies from the requested genre");
        assertEquals(
                actionMovie.getMovieId(),
                result.get(0).getMovieId(),
                "The returned movie should be the one linked to the action genre"
        );
    }

    @Test
    void findByContent_Genres_GenreId_withNoMoviesInGenre_returnsEmptyList() {
        // Arrange: genre exists but has no movies linked to it
        Genre horror = createAndSaveGenre("Horror");

        // Act
        List<Movie> result = movieRepository.findByContent_Genres_GenreId(horror.getGenreId());

        // Assert: returns an empty list — not null — so the caller can show a "no movies found" message
        assertNotNull(result, "Result should never be null");
        assertTrue(result.isEmpty(), "Should return an empty list when no movies exist for the genre");
    }

    @Test
    void findByContent_Genres_GenreId_withNonExistentGenreId_returnsEmptyList() {
        // Arrange: no genres or movies exist at all
        Long nonExistentGenreId = 99999L;

        // Act
        List<Movie> result = movieRepository.findByContent_Genres_GenreId(nonExistentGenreId);

        // Assert: safe empty result — no exception thrown
        assertNotNull(result, "Result should never be null");
        assertTrue(result.isEmpty(), "Should return an empty list for a genre ID that does not exist");
    }

    @Test
    void findByContent_Genres_GenreId_movieWithMultipleGenres_isReturnedForEachGenre() {
        // Arrange: one movie that belongs to both Action and Thriller
        Genre action = createAndSaveGenre("Action");
        Genre thriller = createAndSaveGenre("Thriller");

        // Build content manually so we can assign two genres at once
        Content content = new Content();
        content.setOriginaltitle("Heat");
        content.setTitle("Heat");
        content.setRating(BigDecimal.valueOf(8.3));
        content.setReleasedate(LocalDate.of(1995, 12, 15));
        content.setType(ContentType.MOVIE);
        content.setGenres(List.of(action, thriller));
        Content savedContent = contentRepository.save(content);

        Movie movie = new Movie();
        movie.setDuration((short) 170);
        movie.setContent(savedContent);
        movieRepository.save(movie);

        // Act
        List<Movie> actionResults  = movieRepository.findByContent_Genres_GenreId(action.getGenreId());
        List<Movie> thrillerResults = movieRepository.findByContent_Genres_GenreId(thriller.getGenreId());

        // Assert: the same movie appears under both genres
        assertEquals(1, actionResults.size(),  "Movie should appear under the Action genre");
        assertEquals(1, thrillerResults.size(), "Movie should appear under the Thriller genre");
        assertEquals(
                actionResults.get(0).getMovieId(),
                thrillerResults.get(0).getMovieId(),
                "Both queries should return the same movie"
        );
    }
}