package org.example.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserValidationService {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9æøåÆØÅ]{8,28}$";

    /**
     * Business logic for username validation.
     * Checks: Presence, Whitespace, Length, and Alphanumeric/Danish characters.
     */
    public List<String> validateUsername(String username) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.isBlank()) {
            errors.add("Username must be filled in.");
            return errors; // Return early if null
        }

        if (username.contains(" ")) {
            errors.add("Username must not contain whitespace.");
        }

        if (!Pattern.matches(USERNAME_REGEX, username)) {
            errors.add("Username must be between 8-28 characters and contain only letters (A-Z, æøå) and numbers.");
        }

        return errors;
    }

    /**
     * Business logic for password validation.
     * Checks: Presence, Whitespace, Length, Uppercase, Numbers, and Special characters.
     */
    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isBlank()) {
            errors.add("Password must be filled in.");
            return errors; // Return early if null
        }

        if (password.contains(" ")) {
            errors.add("Password must not contain whitespace.");
        }

        if (password.length() < 8 || password.length() > 28) {
            errors.add("Password must be between 8 and 28 characters.");
        }

        if (!Pattern.compile("[A-ZÆØÅ]").matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter.");
        }

        if (!Pattern.compile("\\d").matcher(password).find()) {
            errors.add("Password must contain at least one number.");
        }

        if (!Pattern.compile("[^a-zA-Z0-9æøåÆØÅ\\s]").matcher(password).find()) {
            errors.add("Password must contain at least one special character.");
        }

        return errors;
    }
}