package org.example.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReviewValidationServiceTest {

    private ReviewValidationService validator;

    @BeforeEach
    void setUp() {
        validator = new ReviewValidationService();
    }

    @Nested
    @DisplayName("Title validation")
    class TitleValidation {

        // ------------------- VALID BOUNDARIES -------------------
        @ParameterizedTest
        @ValueSource(ints = {1, 30, 60})
        @DisplayName("Valid title boundaries should not throw exception")
        void validateTitle_validBoundaries(int length) {
            // Arrange
            String title = textOfLength(length);

            // Act
            Executable act = () -> validator.validateTitle(title);

            // Assert
            assertDoesNotThrow(act);
        }

        // ------------------- INVALID BOUNDARIES -------------------
        @ParameterizedTest
        @ValueSource(ints = {0, 61})
        @DisplayName("Invalid title length boundaries should throw exception")
        void validateTitle_invalidBoundaries(int length) {
            // Arrange
            String invalidTitle = textOfLength(length);

            // Act
            Executable act = () -> validator.validateTitle(invalidTitle);

            // Assert
            assertThrows(IllegalArgumentException.class, act);
        }

        // ------------------- INVALID BOUNDARIES -------------------
        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Missing title should throw exception")
        void validateTitle_missingValues(String title) {
            // Arrange
            String missingTitle = title;

            // Act
            Executable act = () -> validator.validateTitle(missingTitle);

            // Assert
            assertThrows(IllegalArgumentException.class, act);
        }
    }

    @Nested
    @DisplayName("Comment validation")
    class CommentValidation {

        // ------------------- VALID BOUNDARIES -------------------
        @ParameterizedTest
        @ValueSource(ints = {1, 250, 500})
        @DisplayName("Valid comment boundaries should not throw exception")
        void validateComment_validBoundaries(int length) {
            // Arrange
            String comment = textOfLength(length);

            // Act
            Executable act = () -> validator.validateComment(comment);

            // Assert
            assertDoesNotThrow(act);
        }

        // ------------------- INVALID BOUNDARIES -------------------
        @ParameterizedTest
        @ValueSource(ints = {0, 501})
        @DisplayName("Invalid comment length boundaries should throw exception")
        void validateComment_invalidBoundaries(int length) {
            // Arrange
            String invalidComment = textOfLength(length);

            // Act
            Executable act = () -> validator.validateComment(invalidComment);

            // Assert
            assertThrows(IllegalArgumentException.class, act);
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Missing comment should throw exception")
        void validateComment_missingValues(String comment) {
            // Arrange
            String missingComment = comment;

            // Act
            Executable act = () -> validator.validateComment(missingComment);

            // Assert
            assertThrows(IllegalArgumentException.class, act);
        }
    }

    @Nested
    @DisplayName("Rating validation")
    class RatingValidation {

        // ------------------- VALID BOUNDARIES -------------------
        @ParameterizedTest
        @ValueSource(shorts = {1, 5, 10})
        @DisplayName("Valid rating boundaries should not throw exception")
        void validateRating_validBoundaries(short rating) {
            // Arrange
            Short validRating = rating;

            // Act
            Executable act = () -> validator.validateRating(validRating);

            // Assert
            assertDoesNotThrow(act);
        }

        // ------------------- INVALID BOUNDARIES -------------------
        @ParameterizedTest
        @NullSource
        @ValueSource(shorts = {0, 11})
        @DisplayName("Invalid rating boundaries should throw exception")
        void validateRating_invalidBoundaries(Short rating) {
            // Arrange
            Short invalidRating = rating;

            // Act
            Executable act = () -> validator.validateRating(invalidRating);

            // Assert
            assertThrows(IllegalArgumentException.class, act);
        }

        @ParameterizedTest
        @ValueSource(strings = {"4.3"})
        @DisplayName("Decimal rating values should fail")
        void validateRating_decimalValueShouldFail(String rating) {
            // Arrange
            String decimalRating = rating;

            // Act
            Executable act = () -> Short.valueOf(decimalRating);

            // Assert
            assertThrows(NumberFormatException.class, act);
        }
    }

    private String textOfLength(int length) {
        return "a".repeat(length);
    }

}
