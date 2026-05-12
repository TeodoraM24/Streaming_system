package org.example.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodValidationTest {

    private final PaymentMethodValidation validator = new PaymentMethodValidation();

    // =========================================================
    // CARD NUMBER TESTS
    // Requirement: Card number must be exactly 16 numeric digits
    // =========================================================


    // -------------------------
    // Positive tests
    // Valid equivalence partition
    // Valid card numbers with exactly 16 digits
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567812345678",       // valid 16 digits
            "1234567890123456",       // valid 16 digits
            "1111111111111111"        // valid boundary value: 16 digits
    })
    @DisplayName("Valid card numbers should not throw exception")
    void validateCardNumber_validValues(String cardNumber) {
        assertDoesNotThrow(() -> validator.validateCardNumber(cardNumber));
    }


    // -------------------------
    // Negative tests
    // Invalid values: null or empty
    // -------------------------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Null or empty card numbers should throw exception")
    void validateCardNumber_requiredCheck(String cardNumber) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateCardNumber(cardNumber)
        );
    }


    // -------------------------
    // Negative tests
    // Invalid equivalence partitions:
    // - Too short
    // - Too long
    // - Alphabetic
    // - Alphanumeric
    // - Special characters
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678",                // too short
            "1234567890123450000000",  // too long
            "abcdefghijklmnop",        // alphabetic
            "111111111111111#",        // contains special character
            "111111111111111 1",        // contains white space
            "111111111111abcd",        // alphanumeric
            "111111111111111",         // 15 digits, invalid boundary
            "11111111111111111",       // 17 digits, invalid boundary
            "11111111111111",          // 14 digits, invalid boundary
            "111111111111111111",      // 18 digits, invalid boundary
            "1",                       // 1 digit
            "11"                       // 2 digits
    })
    @DisplayName("Invalid card numbers should throw exception")
    void validateCardNumber_invalidValues(String cardNumber) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateCardNumber(cardNumber)
        );
    }


    // =========================================================
    // EXPIRATION MONTH TESTS
    // Requirement: Expiration month must be between 1 and 12
    // =========================================================


    // -------------------------
    // Positive tests
    // Valid equivalence partition + boundary values
    // Valid months: 1-12
    // -------------------------

    @ParameterizedTest
    @ValueSource(shorts = {
            1,      // valid lower boundary
            2,      // valid near lower boundary
            11,     // valid near upper boundary
            12      // valid upper boundary
    })
    @DisplayName("Valid expiration months should not throw exception")
    void validateExpirationMonth_validValues(Short month) {
        assertDoesNotThrow(() -> validator.validateExpirationMonth(month));
    }


    // -------------------------
    // Negative tests
    // Invalid value: null
    // -------------------------

    @ParameterizedTest
    @NullSource
    @DisplayName("Null expiration month should throw exception")
    void validateExpirationMonth_nullValue(Short month) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateExpirationMonth(month)
        );
    }


    // -------------------------
    // Negative tests
    // Invalid equivalence partitions + boundary values:
    // - Month below 1
    // - Month above 12
    // -------------------------

    @ParameterizedTest
    @ValueSource(shorts = {
            0,      // invalid lower boundary
            -1,     // invalid below minimum
            -2,     // invalid below minimum
            13,     // invalid upper boundary
            14,     // invalid above maximum
            25      // invalid high value
    })
    @DisplayName("Invalid expiration months should throw exception")
    void validateExpirationMonth_invalidValues(Short month) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateExpirationMonth(month)
        );
    }


    // =========================================================
    // EXPIRATION YEAR TESTS
    // Requirement: Expiration year must be 2026 or later
    // =========================================================


    // -------------------------
    // Positive tests
    // Valid equivalence partition + boundary values
    // Valid years: 2026-MAX
    // -------------------------

    @ParameterizedTest
    @ValueSource(shorts = {
            2026,   // valid lower boundary
            2027,   // valid near lower boundary
            2030    // valid middle value
    })
    @DisplayName("Valid expiration years should not throw exception")
    void validateExpirationYear_validValues(Short year) {
        assertDoesNotThrow(() -> validator.validateExpirationYear(year));
    }


    // -------------------------
    // Negative tests
    // Invalid value: null
    // -------------------------

    @ParameterizedTest
    @NullSource
    @DisplayName("Null expiration year should throw exception")
    void validateExpirationYear_nullValue(Short year) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateExpirationYear(year)
        );
    }


    // -------------------------
    // Negative tests
    // Invalid equivalence partitions + boundary values:
    // - Year below 2026
    // - Zero
    // -------------------------

    @ParameterizedTest
    @ValueSource(shorts = {
            0,      // invalid special case
            1,      // invalid low value
            2020,   // invalid middle value
            2025    // invalid boundary before valid partition
    })
    @DisplayName("Invalid expiration years should throw exception")
    void validateExpirationYear_invalidValues(Short year) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateExpirationYear(year)
        );
    }


    // =========================================================
    // CVC TESTS
    // Requirement: CVC must be exactly 3 numeric digits
    // =========================================================


    // -------------------------
    // Positive tests
    // Valid equivalence partition
    // Valid CVC values with exactly 3 digits
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "123",      // valid 3 digits
            "111",      // valid 3 digits
            "999"       // valid 3 digits
    })
    @DisplayName("Valid CVC values should not throw exception")
    void validateCvc_validValues(String cvc) {
        assertDoesNotThrow(() -> validator.validateCvc(cvc));
    }


    // -------------------------
    // Negative tests
    // Invalid values: null or empty
    // -------------------------

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Null or empty CVC should throw exception")
    void validateCvc_requiredCheck(String cvc) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateCvc(cvc)
        );
    }


    // -------------------------
    // Negative tests
    // Invalid equivalence partitions:
    // - Too short
    // - Too long
    // - Alphanumeric
    // - Special characters
    // - White space
    // - Alphabetic character
    // -------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "12",        // too short
            "1234678",   // too long
            "12H",       // contains alphabetic character
            "12?",       // contains special character
            "12 3",      // contains white space
            "1b3",       // alphanumeric
            "1111",      // 4 characters, invalid boundary
            "11111"      // 5 characters, invalid high value
    })
    @DisplayName("Invalid CVC values should throw exception")
    void validateCvc_invalidValues(String cvc) {
        assertThrows(
                ResponseStatusException.class,
                () -> validator.validateCvc(cvc)
        );
    }
}