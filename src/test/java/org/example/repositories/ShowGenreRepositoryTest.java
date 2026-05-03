package org.example.repositories;

import org.example.entities.Content;
import org.example.entities.Genre;
import org.example.entities.Movie;
import org.example.entities.Show;
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
class ShowGenreRepositoryTest {

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
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        showRepository.deleteAll();
        contentRepository.deleteAll();
        genreRepository.deleteAll();
    }

    private Genre createAndSaveGenre(String name) {
        Genre genre = new Genre();
        genre.setGenrename(name);
        return genreRepository.save(genre);
    }

    private Show createAndSaveShowWithGenre(String title, Genre genre) {
        Content content = new Content();
        content.setOriginaltitle(title);
        content.setTitle(title);
        content.setRating(BigDecimal.valueOf(7.5));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.SHOW);
        content.setGenres(List.of(genre));

        Content savedContent = contentRepository.save(content);

        Show show = new Show();
        show.setContent(savedContent);

        return showRepository.save(show);
    }

    private Movie createAndSaveMovieWithGenre(String title, Genre genre) {
        Content content = new Content();
        content.setOriginaltitle(title);
        content.setTitle(title);
        content.setRating(BigDecimal.valueOf(8.0));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.MOVIE);
        content.setGenres(List.of(genre));

        Content savedContent = contentRepository.save(content);

        Movie movie = new Movie();
        movie.setDuration((short) 120);
        movie.setContent(savedContent);

        return movieRepository.save(movie);
    }

    @Test
    void findByContent_Genres_GenreId_returnsShowsForThatGenre() {
        Genre drama = createAndSaveGenre("Drama");
        createAndSaveShowWithGenre("Breaking Bad", drama);
        createAndSaveShowWithGenre("The Wire", drama);

        List<Show> result = showRepository.findByContent_Genres_GenreId(drama.getGenreId());

        assertEquals(2, result.size(), "Should return all shows linked to the genre");
    }

    @Test
    void findByContent_Genres_GenreId_returnsOnlyShowsFromSelectedGenre() {
        Genre drama = createAndSaveGenre("Drama");
        Genre comedy = createAndSaveGenre("Comedy");
        Show dramaShow = createAndSaveShowWithGenre("Breaking Bad", drama);
        createAndSaveShowWithGenre("The Office", comedy);

        List<Show> result = showRepository.findByContent_Genres_GenreId(drama.getGenreId());

        assertEquals(1, result.size(), "Should return only shows from the requested genre");
        assertEquals(dramaShow.getShowsId(), result.get(0).getShowsId());
    }

    @Test
    void findByContent_Genres_GenreId_doesNotReturnMoviesFromSelectedGenre() {
        Genre drama = createAndSaveGenre("Drama");
        Show dramaShow = createAndSaveShowWithGenre("Breaking Bad", drama);
        createAndSaveMovieWithGenre("The Godfather", drama);

        List<Show> result = showRepository.findByContent_Genres_GenreId(drama.getGenreId());

        assertEquals(1, result.size(), "Movies in the same genre should not be returned");
        assertEquals(dramaShow.getShowsId(), result.get(0).getShowsId());
    }

    @Test
    void findByContent_Genres_GenreId_withNoShowsInGenre_returnsEmptyList() {
        Genre documentary = createAndSaveGenre("Documentary");

        List<Show> result = showRepository.findByContent_Genres_GenreId(documentary.getGenreId());

        assertNotNull(result, "Result should never be null");
        assertTrue(result.isEmpty(), "Caller can use an empty list to show a no-series-found message");
    }

    @Test
    void findByContent_Genres_GenreId_showWithMultipleGenres_isReturnedForEachGenre() {
        Genre drama = createAndSaveGenre("Drama");
        Genre thriller = createAndSaveGenre("Thriller");

        Content content = new Content();
        content.setOriginaltitle("Dark");
        content.setTitle("Dark");
        content.setRating(BigDecimal.valueOf(8.7));
        content.setReleasedate(LocalDate.of(2017, 12, 1));
        content.setType(ContentType.SHOW);
        content.setGenres(List.of(drama, thriller));
        Content savedContent = contentRepository.save(content);

        Show show = new Show();
        show.setContent(savedContent);
        showRepository.save(show);

        List<Show> dramaResults = showRepository.findByContent_Genres_GenreId(drama.getGenreId());
        List<Show> thrillerResults = showRepository.findByContent_Genres_GenreId(thriller.getGenreId());

        assertEquals(1, dramaResults.size(), "Show should appear under the Drama genre");
        assertEquals(1, thrillerResults.size(), "Show should appear under the Thriller genre");
        assertEquals(dramaResults.get(0).getShowsId(), thrillerResults.get(0).getShowsId());
    }
}
