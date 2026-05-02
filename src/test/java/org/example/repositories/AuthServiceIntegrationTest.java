package org.example.repositories;


import org.example.entities.User;
import org.example.enums.Role;
import org.example.security.DTOs.AuthResponse;
import org.example.security.DTOs.RegisterRequest;
import org.example.security.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@TestPropertySource(properties = {
        "app.neo4j.migrations.enabled=false"
})
class AuthServiceIntegrationTest {

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
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        accountRepository.deleteAll();
    }

    // ─── Helper ──────────────────────────────────────────────────────────────────

    private RegisterRequest validRequest(String username, String mail) {
        return new RegisterRequest(username, "Secret123!!", "John", "Doe", "12345678", mail);
    }

    // ─── Happy Path ──────────────────────────────────────────────────────────────

    @Test
    void register_validRequest_returnsTokenAndIds() {
        AuthResponse response = authService.register(validRequest("jdotj123", "john@example.com"));

        assertNotNull(response.getToken(), "JWT token should be returned");
        assertNotNull(response.getUsersId(), "User ID should be returned");
        assertNotNull(response.getAccountId(), "Account ID should be returned");
        assertEquals("jdotj123", response.getUsername());
    }

    @Test
    void register_validRequest_persistsUserAndAccountCorrectly() {
        authService.register(new RegisterRequest(
                "jdotj123", "Secret123!!", "John", "Doe", "12345678", "john@example.com"
        ));

        User savedUser = userRepository.findByUsername("jdotj123")
                .orElseThrow(() -> new AssertionError("User not found after registration"));

        // Verify user fields
        assertEquals("jdotj123", savedUser.getUsername());
        assertEquals(Role.USER, savedUser.getRole(), "Self-registered users should get USER role");

        // Verify account fields persisted correctly via the cascade
        assertNotNull(savedUser.getAccount(), "Account should be linked to user");
        assertEquals("john@example.com", savedUser.getAccount().getMail());
        assertEquals("John", savedUser.getAccount().getFirstname());
        assertEquals("Doe", savedUser.getAccount().getLastname());
        assertEquals("12345678", savedUser.getAccount().getPhonenumber());

        // Cross-verify from account side
        accountRepository.findByMail("john@example.com")
                .orElseThrow(() -> new AssertionError("Account not found by mail after registration"));
    }

    // ─── Validation wired in ─────────────────────────────────────────────────────

    @Test
    void register_invalidCredentials_throwsBadRequest() {
        // Proves validation is called by AuthService — detail is covered in UserValidationServiceTest
        RegisterRequest request = new RegisterRequest(
                "ab", "weak", "John", "Doe", "12345678", "john@example.com"
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
    // ─── Duplicate Tests ─────────────────────────────────────────────────────────

    @Test
    void register_duplicateUsername_throwsConflict() {
        authService.register(validRequest("jdotj123", "john@example.com"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRequest("jdotj123", "jane@example.com")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void register_duplicateMail_throwsConflict() {
        authService.register(validRequest("jdotj123", "john@example.com"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRequest("janedoe1", "john@example.com")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}