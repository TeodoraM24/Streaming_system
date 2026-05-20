package org.example.neo4j.config;

import lombok.RequiredArgsConstructor;
import org.example.neo4j.service.MigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.neo4j.migrations.enabled", havingValue = "true")
public class MigrationRunnerConfig {

    private final MigrationService migrationService;

    @Bean
    public CommandLineRunner runMigration() {
        return args -> {
            migrationService.migrateAll();
            System.out.println("Neo4j migration finished");
        };
    }
}