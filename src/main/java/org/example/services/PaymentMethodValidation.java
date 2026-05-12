package org.example.services;

import org.example.enums.PaymentType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PaymentMethodValidation {

    public void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number is required");
        }

        if (!cardNumber.matches("\\d{16}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number must be 16 digits");
        }
    }

    public void validateExpirationMonth(Short expirationMonth) {
        if (expirationMonth == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration month is required");
        }

        if (expirationMonth < 1 || expirationMonth > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration month must be between 1 and 12");
        }
    }

    public void validateExpirationYear(Short expirationYear) {
        if (expirationYear == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration year is required");
        }

        if (expirationYear < 2026) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration year cannot be in the past");
        }
    }

    public void validateCvc(String cvc) {
        if (cvc == null || cvc.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CVC is required");
        }

        if (!cvc.matches("\\d{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CVC must be 3 digits");
        }
    }

    public void validateType(PaymentType type) {
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment type is required");
        }
    }

    public void validateAccountId(Long accountId) {
        if (accountId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account id is required");
        }

        if (accountId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account id must be greater than 0");
        }
    }
}