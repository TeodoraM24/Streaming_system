package org.example.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountValidationTest {

    private final AccountValidation validator = new AccountValidation();

    @ParameterizedTest
    @ValueSource(strings = {
            "Abcdefghijklmno",              // Valid: Middle value
            "Åse",                          // Valid: Danish letter
            "Adam",                         // Valid: First letter uppercase, remaining lowercase
            "A",                            // Valid: 1 char lower boundary
            "Ab",                           // Valid: 2 chars lower boundary
            "Abcdefghijklmnopqrstuvwxyzaaa", // Valid: 29 chars upper boundary
            "Abcdefghijklmnopqrstuvwxyzaaaa" // Valid: 30 chars upper boundary
    })
    @DisplayName("Valid account names return no errors")
    void validateName_validValues(String name) {
        List<String> errors = validator.validateName(name);

        assertTrue(errors.isEmpty(), "Should have no errors for: " + name);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                              // Invalid: Empty
            "Abcdefghijklmnopqrstuvwxyzaaaaa", // Invalid: 31 chars
            "Abcdefghijklmnopqrstuvwxyzabcdefghij", // Invalid: Length > 30
            "Mads1",                         // Invalid: Contains digit
            "Mads!",                         // Invalid: Contains special character
            "Madçois",                       // Invalid: Contains non-Danish character
            "mads",                          // Invalid: First letter not uppercase
            "MADS"                           // Invalid: More than first letter is uppercase
    })
    @DisplayName("Invalid account names return error list")
    void validateName_invalidValues(String name) {
        List<String> errors = validator.validateName(name);

        assertFalse(errors.isEmpty(), "Should have errors for: " + name);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "21234567", // Valid: 1-digit prefix
            "30123456", // Valid: 2-digit prefix
            "34212345", // Valid: 3-digit prefix
            "34612345", // Valid: 3-digit prefix range 344-349
            "31123456", // Valid: 2-digit prefix
            "40123456", // Valid: 2-digit prefix
            "53123456", // Valid: 2-digit prefix
            "71123456", // Valid: 2-digit prefix
            "34912345", // Valid: upper boundary in 344-349 range
            "35612345", // Valid: lower boundary in 356-357 range
            "49812345", // Valid: lower boundary in 498-499 range
            "82912345"  // Valid: 3-digit prefix
    })
    @DisplayName("Valid account phone numbers return no errors")
    void validatePhoneNumber_validValues(String phoneNumber) {
        List<String> errors = validator.validatePhoneNumber(phoneNumber);

        assertTrue(errors.isEmpty(), "Should have no errors for: " + phoneNumber);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "1234567",   // Invalid: Less than 8 digits
            "123456789", // Invalid: More than 8 digits
            "10123456",  // Invalid: Prefix not allowed
            "3012345",   // Invalid: Correct prefix but wrong length
            "30A23456",  // Invalid: Non-numeric character
            "34312345",  // Invalid: Prefix partially matching but invalid
            "35012345",  // Invalid: Outside 344-349 range
            "35812345",  // Invalid: Outside 356-357 range
            "49712345",  // Invalid: Outside 493-496 and 498-499 ranges
            ""           // Invalid: Empty
    })
    @DisplayName("Invalid account phone numbers return error list")
    void validatePhoneNumber_invalidValues(String phoneNumber) {
        List<String> errors = validator.validatePhoneNumber(phoneNumber);

        assertFalse(errors.isEmpty(), "Should have errors for: " + phoneNumber);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "text@domain.tld"
    })
    @DisplayName("Valid account emails return no errors")
    void validateEmail_validValues(String email) {
        List<String> errors = validator.validateEmail(email);

        assertTrue(errors.isEmpty(), "Should have no errors for: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "textdomain.tld",    // Invalid: Missing @
            "@domain.tld",       // Invalid: Missing text before @
            "text@.tld",         // Invalid: Missing domain
            "text@domain",       // Invalid: Missing TLD
            "text!@domain.tld",  // Invalid: Special character before @
            "text@domain!.tld",  // Invalid: Special character in domain
            "text@domain.tld1",  // Invalid: Number in TLD
            "text@domain.tld!",  // Invalid: Special character in TLD
            "text@domaintld"     // Invalid: TLD missing .
    })
    @DisplayName("Invalid account emails return error list")
    void validateEmail_invalidValues(String email) {
        List<String> errors = validator.validateEmail(email);

        assertFalse(errors.isEmpty(), "Should have errors for: " + email);
    }
}
