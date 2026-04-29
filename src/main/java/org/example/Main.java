package org.example;

import org.example.entities.Receipt;
import org.example.repositories.ReceiptRepository;
import org.example.services.ReceiptValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}