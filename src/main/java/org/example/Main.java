package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // This is the "Magic" annotation that finds your controllers and repos
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}