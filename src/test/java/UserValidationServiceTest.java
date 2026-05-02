import org.example.services.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationServiceTest {

    private UserValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new UserValidationService();
    }

    // --------------------------------------------------------------
    // USERNAME TESTS
    // --------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "AnneØrå9",                     // Valid: Danish chars + Alphanumeric
            "Abcdefgh",                     // Valid: Lower boundary (8 chars)
            "Abcdefghijklmnopqrstuvwxyzab", // Valid: Upper boundary (28 chars)
            "12345678" // Valid: Numbers only (8 chars)
    })
    @DisplayName("Valid usernames return no errors")
    void validateUsername_validValues(String username) {
        List<String> errors = validationService.validateUsername(username);
        assertTrue(errors.isEmpty(), "Should have no errors for: " + username);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",                             // Invalid: Empty
            "Anne123!",                     // Invalid: Special character (!)
            "Anna 123",                     // Invalid: Whitespace
            "Abcdefg",                      // Invalid: Too short (7 chars)
            "Abcdefghijklmnopqrstuvwxyzabc",// Invalid: Too long (29 chars)
            "François"                    // Invalid: 'ç' is not in A-Z or ÆØÅ

    })
    @DisplayName("Invalid usernames return error list")
    void validateUsername_invalidValues(String username) {
        List<String> errors = validationService.validateUsername(username);
        assertFalse(errors.isEmpty(), "Should have errors for: " + username);
    }

    // --------------------------------------------------------------
    // PASSWORD TESTS
    // --------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "Password123!",                 // Valid: Normal
            "Æøå1234!",                     // Valid: Danish characters
            "Abcdef1!",                     // Valid: Lower boundary (8 chars)
            "Abcdefghijklmnopqrstuvwxyz1!", // Valid: Upper boundary (28 chars)
            "François2!"                    // Valid: Special char + Number + Upper (ç is special)
    })
    @DisplayName("Valid passwords return no errors")
    void validatePassword_validValues(String password) {
        List<String> errors = validationService.validatePassword(password);
        assertTrue(errors.isEmpty(), "Should have no errors for: " + password);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",                             // Invalid: Empty
            "Password123",                  // Invalid: Missing special char
            "Pass word1!",                  // Invalid: Whitespace
            "password!!!",                  // Invalid: Missing Number/Uppercase
            "Abcde1!",                      // Invalid: Too short (7 chars)
            "Abcdefghijklmnopqrstuvwxyza1!",// Invalid: Too long (29 chars)
    })
    @DisplayName("Invalid passwords return error list")
    void validatePassword_invalidValues(String password) {
        List<String> errors = validationService.validatePassword(password);
        assertFalse(errors.isEmpty(), "Should have errors for: " + password);
    }

}