package org.example.services;

import org.example.entities.Receipt;
import org.example.repositories.ReceiptRepository;

import java.math.BigDecimal;


public class ReceiptValidation {


    public void validateReceiptNumber(String receiptNumber) {

        // 1. Required check
        if (receiptNumber == null || receiptNumber.isBlank()) {
            throw new IllegalArgumentException("Receipt number must be provided");
        }

        // 2. Numeric only check
        if (!receiptNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Receipt number must contain only digits");
        }

        // 3. Length check
        if (receiptNumber.length() < 10 || receiptNumber.length() > 15) {
            throw new IllegalArgumentException(
                    "Receipt number must be between 10 and 15 digits"
            );
        }

    }

    private static final BigDecimal MIN_PRICE = new BigDecimal("99.00");
    private static final BigDecimal MAX_PRICE = new BigDecimal("999.99");

    public void validateReceiptPrice(BigDecimal price) {

        if (price == null) {
            throw new IllegalArgumentException("Price must be provided");
        }

        if (price.compareTo(MIN_PRICE) < 0) {
            throw new IllegalArgumentException("Price must be at least 99.00");
        }

        if (price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price must be no more than 999.99");
        }
    }

}





