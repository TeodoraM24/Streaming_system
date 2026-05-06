package org.example.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.dtos.PaymentMethodDTO;
import org.example.enums.PaymentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PaymentMethodDTO validDTO() {
        return new PaymentMethodDTO(
                null,
                "1234567812345678",
                (short) 5,
                (short) 2026,
                "123",
                PaymentType.CARD, // skift hvis din enum hedder noget andet
                true,
                1L
        );
    }

    @Test
    @DisplayName("Positive test - valid payment method")
    void validPaymentMethod_shouldPassValidation() {
        PaymentMethodDTO dto = validDTO();

        Set<ConstraintViolation<PaymentMethodDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Negative test - card number has 15 digits")
    void cardNumber15Digits_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCardNumber("123456789012345");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - card number has 17 digits")
    void cardNumber17Digits_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCardNumber("12345678901234567");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - card number contains letters")
    void cardNumberWithLetters_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCardNumber("12345678abcd5678");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - card number is empty")
    void cardNumberEmpty_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCardNumber("");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - card number is null")
    void cardNumberNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCardNumber(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - month below 1")
    void monthBelow1_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setExpirationMonth((short) 0);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - month above 12")
    void monthAbove12_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setExpirationMonth((short) 13);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - month is null")
    void monthNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setExpirationMonth(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - year before 2026")
    void yearBefore2026_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setExpirationYear((short) 2025);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - year is null")
    void yearNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setExpirationYear(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - CVC has 2 digits")
    void cvc2Digits_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCvc("12");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - CVC has 4 digits")
    void cvc4Digits_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCvc("1234");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - CVC contains letters")
    void cvcLetters_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCvc("abc");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - CVC is empty")
    void cvcEmpty_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCvc("");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - CVC is null")
    void cvcNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setCvc(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - type is null")
    void typeNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setType(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Negative test - accountId is null")
    void accountIdNull_shouldFailValidation() {
        PaymentMethodDTO dto = validDTO();
        dto.setAccountId(null);

        assertFalse(validator.validate(dto).isEmpty());
    }
}