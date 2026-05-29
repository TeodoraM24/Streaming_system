package org.example.repositories;

import org.example.entities.Content;
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

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ShowRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("postgres/init_types.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure Spring to use Testcontainers PostgreSQL database
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ContentRepository contentRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test to ensure isolation
        showRepository.deleteAll();
        contentRepository.deleteAll();
    }

    // Helper method: creates a Content + Show and saves both
    private Show createAndSaveShow(String title, double rating) {
        Content content = new Content();
        content.setOriginaltitle(title);
        content.setTitle(title);
        content.setRating(BigDecimal.valueOf(rating));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.SHOW);

        Content savedContent = contentRepository.save(content);

        Show show = new Show();
        show.setContent(savedContent);

        return showRepository.save(show);
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_returnsAtMostTen() {
        // Creates 12 shows with increasing ratings
        for (int i = 1; i <= 12; i++) {
            createAndSaveShow("Show " + i, i);
        }

        // Calls repository method to get top 10 highest rated shows
        List<Show> top10 = showRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies that only 10 shows are returned
        assertEquals(10, top10.size(), "Should return exactly 10 shows");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_returnsInDescendingOrder() {
        // Creates 3 shows with different ratings
        createAndSaveShow("Low Rated", 4.0);
        createAndSaveShow("Mid Rated", 6.5);
        createAndSaveShow("High Rated", 9.2);

        // Fetches top-rated shows
        List<Show> top10 = showRepository.findTop10ByOrderByContent_RatingDesc();

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
    void findTop10ByOrderByContent_RatingDesc_withFewerThanTenShows_returnsAll() {
        // Creates only one show
        createAndSaveShow("Only Show", 7.5);

        // Fetches top 10 (but only 1 exists)
        List<Show> top10 = showRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies all available shows are returned
        assertEquals(1, top10.size(), "Should return all shows when fewer than 10 exist");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_withNoShows_returnsEmptyList() {
        // Calls repository when database is empty
        List<Show> top10 = showRepository.findTop10ByOrderByContent_RatingDesc();

        // Verifies safe empty result (not null, just empty)
        assertNotNull(top10);
        assertTrue(top10.isEmpty(), "Should return an empty list when no shows exist");
    }

    @Test
    void findTop10ByOrderByContent_RatingDesc_doesNotReturnAscendingOrder() {
        // Creates shows with increasing ratings
        createAndSaveShow("A", 1.0);
        createAndSaveShow("B", 5.0);
        createAndSaveShow("C", 10.0);

        // Fetches results
        List<Show> result = showRepository.findTop10ByOrderByContent_RatingDesc();

        // Ensures lowest-rated show is NOT first (basic sanity check)
        assertNotEquals(
                1.0,
                result.get(0).getContent().getRating().doubleValue(),
                "Lowest rated show should not be first"
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
