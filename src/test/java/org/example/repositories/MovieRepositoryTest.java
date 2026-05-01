package org.example.repositories;

import org.example.entities.Content;
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

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class MovieRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("postgres/init_types.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure Spring to use Testcontainers PostgreSQL database
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ContentRepository contentRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test to ensure isolation
        movieRepository.deleteAll();
        contentRepository.deleteAll();
    }

    // Helper method: creates a Content + Movie and saves both
    private Movie createAndSaveMovie(String title, double rating) {
        Content content = new Content();
        content.setOriginaltitle(title);
        content.setTitle(title);
        content.setRating(BigDecimal.valueOf(rating));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.MOVIE);

        Content savedContent = contentRepository.save(content);

        Movie movie = new Movie();
        movie.setDuration((short) 120);
        movie.setContent(savedContent);

        return movieRepository.save(movie);
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_returnsAtMostTen() {
        // Creates 12 movies with increasing ratings
        for (int i = 1; i <= 12; i++) {
            createAndSaveMovie("Movie " + i, i);
        }

        // Calls repository method to get top 10 highest rated movies
        List<Movie> top10 = movieRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies that only 10 movies are returned
        assertEquals(10, top10.size(), "Should return exactly 10 movies");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_returnsInDescendingOrder() {
        // Creates 3 movies with different ratings
        createAndSaveMovie("Low Rated", 4.0);
        createAndSaveMovie("Mid Rated", 6.5);
        createAndSaveMovie("High Rated", 9.2);

        // Fetches top-rated movies
        List<Movie> top10 = movieRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies correct sorting: highest to lowest rating
        assertEquals(3, top10.size());
        assertTrue(
                top10.get(0).getContent().getRating()
                        .compareTo(top10.get(1).getContent().getRating()) > 0,
                "First result should have a higher rating than second"
        );
        assertTrue(
                top10.get(1).getContent().getRating()
                        .compareTo(top10.get(2).getContent().getRating()) > 0,
                "Second result should have a higher rating than third"
        );
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_withFewerThanTenMovies_returnsAll() {
        // Creates only one movie
        createAndSaveMovie("Only Movie", 7.5);

        // Fetches top 10 (but only 1 exists)
        List<Movie> top10 = movieRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies all available movies are returned
        assertEquals(1, top10.size(), "Should return all movies when fewer than 10 exist");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_withNoMovies_returnsEmptyList() {
        // Calls repository when database is empty
        List<Movie> top10 = movieRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies safe empty result (not null, just empty)
        assertNotNull(top10);
        assertTrue(top10.isEmpty(), "Should return an empty list when no movies exist");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_doesNotReturnAscendingOrder() {
        // Creates movies with increasing ratings
        createAndSaveMovie("A", 1.0);
        createAndSaveMovie("B", 5.0);
        createAndSaveMovie("C", 10.0);

        // Fetches results
        List<Movie> result = movieRepository.findTop10ByOrderByContent_RatingDesc();

        // Ensures lowest-rated movie is NOT first (basic sanity check)
        assertNotEquals(
                1.0,
                result.get(0).getContent().getRating().doubleValue(),
                "Lowest rated movie should not be first"
        );
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void printDataSource() throws Exception {
        // Debug helper: prints active datasource and JDBC URL
        System.out.println("DataSource = " + dataSource);
        System.out.println("URL = " + dataSource.getConnection().getMetaData().getURL());
    }
}