package org.example.repositories;

import org.example.entities.Account;
import org.example.entities.Content;
import org.example.entities.Profile;
import org.example.entities.Review;
import org.example.enums.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ReviewRepositoryIntegrationTest {

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
    private ReviewRepository reviewRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        profileRepository.deleteAll();
        accountRepository.deleteAll();
        contentRepository.deleteAll();
    }

    @Nested
    @DisplayName("Test database setup")
    class TestDatabaseSetup {

        @Test
        @DisplayName("Repository test uses Testcontainers database")
        void dataSource_usesTestcontainersDatabase() throws SQLException {
            // Arrange
            String expectedUrl = postgres.getJdbcUrl();

            // Act
            String actualUrl;
            try (Connection connection = dataSource.getConnection()) {
                actualUrl = connection.getMetaData().getURL();
            }

            // Assert
            assertEquals(expectedUrl, actualUrl);
        }
    }

    @Nested
    @DisplayName("Create review")
    class CreateReview {

        @Test
        @DisplayName("Valid review should be saved")
        void saveReview_validReview_persistsReview() {
            // Arrange
            Review review = validReview();

            // Act
            Review savedReview = reviewRepository.saveAndFlush(review);

            // Assert
            assertNotNull(savedReview.getReviewId());
            assertEquals(textOfLength(30), savedReview.getTitle());
            assertEquals((short) 5, savedReview.getRating());
            assertEquals(textOfLength(250), savedReview.getComment());
            assertEquals(1, reviewRepository.count());
        }
    }

    private Review validReview() {
        Account account = createAndSaveAccount();
        Profile profile = createAndSaveProfile(account);
        Content content = createAndSaveContent();

        Review review = new Review();
        review.setTitle(textOfLength(30));
        review.setRating((short) 5);
        review.setComment(textOfLength(250));
        review.setCreatedAt(LocalDateTime.now());
        review.setProfile(profile);
        review.setContent(content);

        return review;
    }

    private Account createAndSaveAccount() {
        Account account = new Account();
        account.setFirstname("Jane");
        account.setLastname("Doe");
        account.setPhonenumber("12345678");
        account.setMail("jane" + System.nanoTime() + "@example.com");

        return accountRepository.save(account);
    }

    private Profile createAndSaveProfile(Account account) {
        Profile profile = new Profile();
        profile.setProfilename("Main profile");
        profile.setAccount(account);

        return profileRepository.save(profile);
    }

    private Content createAndSaveContent() {
        Content content = new Content();
        content.setOriginaltitle("Integration Test Movie");
        content.setTitle("Integration Test Movie");
        content.setDescription("Used by review repository integration tests");
        content.setRating(BigDecimal.valueOf(8.5));
        content.setReleasedate(LocalDate.of(2020, 1, 1));
        content.setType(ContentType.MOVIE);

        return contentRepository.save(content);
    }

    private String textOfLength(int length) {
        return "a".repeat(length);
    }
}
