package org.example.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptPriceValidationTest {

    private final ReceiptValidation validator = new ReceiptValidation();

    // -------------------------
    // Positive tests
    // Valid equivalence partition + boundary values (99.00–999.99)
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "499.99",   // valid middle value
            "99.00",    // valid lower boundary
            "99.01",    // valid lower boundary
            "999.98",   // valid upper boundary
            "999.99"    // valid upper boundary
    })
    @DisplayName("Valid receipt prices should not throw exception")
    void validateReceiptPrice_validValues(String price) {
        BigDecimal value = new BigDecimal(price);

        assertDoesNotThrow(() -> validator.validateReceiptPrice(value));
    }

    // -------------------------
    // Negative tests - Required check
    // Null values
    // -------------------------

    @ParameterizedTest
    @NullSource
    @DisplayName("Null price should throw exception")
    void validateReceiptPrice_requiredCheck(BigDecimal price) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptPrice(price)
        );

        assertEquals("Price must be provided", ex.getMessage());
    }

    // -------------------------
    // Negative tests - Invalid equivalence partition + boundary values (99.00–999.99)
    // -------------------------

    @ParameterizedTest
    @CsvSource({
            "49.50, Price must be at least 99.00",        // invalid middle value of price < 99.00
            "0, Price must be at least 99.00",            // invalid equivalence partition: 0
            "1200.95, Price must be no more than 999.99", // Price larger than 999.99
            "-5, Price must be at least 99.00",           // negative number
            "-0.99, Price must be at least 99.00",        // invalid 3 values approach
            "0.01, Price must be at least 99.00",         // invalid 3 values approach
            "98.98, Price must be at least 99.00",        // invalid 3 values approach
            "98.99, Price must be at least 99.00",        // invalid lower boundary
            "1000.00, Price must be no more than 999.99", // invalid upper boundary
            "1000.01, Price must be no more than 999.99", // invalid 3 values approach

    })
    void validateReceiptPrice_invalidPrices(String price, String expectedMessage) {

        BigDecimal value = new BigDecimal(price);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptPrice(value)
        );

        assertEquals(expectedMessage, ex.getMessage());
    }

    // -------------------------
    // Negative tests - Invalid format
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "abc"   // alphabetic
    })
    @DisplayName("Invalid numeric format should be rejected (if parsed externally)")
    void validateReceiptPrice_invalidFormat(String price) {
        assertThrows(
                NumberFormatException.class,
                () -> new BigDecimal(price)
        );
    }

    // -------------------------
    // Edge case - MAX integer input
    // -------------------------

    @Test
    @DisplayName("Integer.MAX_VALUE should be rejected as out-of-range price")
    void validateReceiptPrice_integerMaxValue_shouldBeInvalid() {

        BigDecimal value = new BigDecimal(Integer.MAX_VALUE);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptPrice(value)
        );

        assertEquals("Price must be no more than 999.99", ex.getMessage());
    }
}