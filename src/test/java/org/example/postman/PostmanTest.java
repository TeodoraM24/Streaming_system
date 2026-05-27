package org.example.postman;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "server.servlet.context-path=/api",
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
        }
)
class PostmanTest {

    private static final Path COLLECTION = Path.of("Postman", "StreamFlix.postman_collection.json");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("streaming_system")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("postgres/small-db-for-postman.sql");

    @LocalServerPort
    int port;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.neo4j.migrations.enabled", () -> "false");
        registry.add("app.mongo.migrations.enabled", () -> "false");
    }

    @Test
    void streamflixPostmanCollectionPassesAgainstTestcontainerDatabase() throws IOException, InterruptedException {
        assertTrue(Files.exists(COLLECTION), "Postman collection not found: " + COLLECTION.toAbsolutePath());

        List<String> command = List.of(
                "npx", "--yes", "newman", "run", COLLECTION.toString(),
                "--color", "off",
                "--env-var", "domain=http://localhost:" + port,
                "--env-var", "bearerToken=",
                "--env-var", "paymentmethodId=1"
        );

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Outcomment sout to remove output
        System.out.println(output);

        assertEquals(0, process.waitFor(), "Newman collection run failed:\n" + output);
    }
}
