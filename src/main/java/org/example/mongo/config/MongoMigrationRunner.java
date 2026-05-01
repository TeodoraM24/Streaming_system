package org.example.mongo.config;

import lombok.RequiredArgsConstructor;
import org.example.mongo.service.MongoMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mongo.migrations.enabled", havingValue = "true")
public class MongoMigrationRunner implements CommandLineRunner {

    private final MongoMigrationService mongoMigrationService;

    @Override
    public void run(String... args) {
//        mongoMigrationService.migrateAll();
//        System.out.println("MongoDB migration finished");
    }
}
