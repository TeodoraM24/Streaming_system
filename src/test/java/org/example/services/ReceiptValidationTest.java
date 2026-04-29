package org.example.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptValidationTest {

    private final ReceiptValidation validator = new ReceiptValidation();

    // -------------------------
    // Positive tests
    // Valid equivalence partition + boundary values (10–15 digits)
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789123",        // 12 digits (valid middle value)
            "1111111111",          // 10 digits (valid lower boundary)
            "11111111111",         // 11 digits (valid lower boundary)
            "11111111111111",      // 14 digits (valid upper boundary)
            "111111111111111"      // 15 digits (valid upper boundary)
    })
    @DisplayName("Valid receipt numbers should not throw exception")
    void validateReceiptNumber_validValues(String receiptNumber) {
        assertDoesNotThrow(() -> validator.validateReceiptNumber(receiptNumber));
    }

    // -------------------------
    // Negative tests - Required check
    // Empty or null values
    // -------------------------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Null or empty receipt numbers should throw exception")
    void validateReceiptNumber_requiredCheck(String receiptNumber) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptNumber(receiptNumber)
        );

        assertEquals("Receipt number must be provided", ex.getMessage());
    }

    // -------------------------
    // Negative tests - Numeric only check
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "12345abcde",            // alphanumeric
            "abcdefghij"             // alphabetic
    })
    @DisplayName("Non-numeric receipt numbers should throw exception")
    void validateReceiptNumber_numericOnlyCheck(String receiptNumber) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptNumber(receiptNumber)
        );

        assertEquals("Receipt number must contain only digits", ex.getMessage());
    }

    // -------------------------
    // Negative tests
    // Invalid equivalence partition + boundary values (1–9 digits and 16-MAX)
    // -------------------------

    @ParameterizedTest
    @CsvSource({
            "1234",                    // 4 digits (invalid middle value)
            "12345678912345678912",    // skal rettes til MAX value systemet kan indeholde
            "1",                       // 1 digit (invalid lower boundary for partitioning: 1–9)
            "11",                      // 2 digits (invalid lower boundary for partitioning: 1–9)
            "11111111",                // 8 digits (invalid upper boundary for partitioning: 1–9)
            "111111111",               // 9 digits (invalid upper boundary for partitioning: 1–9)
            "1111111111111111",        // 16 digits (invalid lower boundary for partitioning: 16-MAX)
            "11111111111111111"        // 17 digits (invalid lower boundary for partitioning: 16-MAX)
    })
    @DisplayName("Invalid receipt length should throw exception")
    void validateReceiptNumber_lengthCheck(String receiptNumber) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateReceiptNumber(receiptNumber)
        );

        assertEquals(
                "Receipt number must be between 10 and 15 digits",
                ex.getMessage()
        );
    }
}