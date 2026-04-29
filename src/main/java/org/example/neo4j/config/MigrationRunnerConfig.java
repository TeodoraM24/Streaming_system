package org.example.neo4j.config;

import lombok.RequiredArgsConstructor;
import org.example.neo4j.service.MigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MigrationRunnerConfig {

    private final MigrationService migrationService;

//    @Bean
//    public CommandLineRunner runMigration() {
//        return args -> migrationService.migrateAll();
//    }
}