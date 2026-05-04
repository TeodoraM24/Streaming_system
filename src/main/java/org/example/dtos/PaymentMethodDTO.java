package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.PaymentMethod;
import org.example.enums.PaymentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethodDTO {

    private Long paymentmethodId;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")

    private String cardNumber;
    @NotNull
    @Min(1)
    @Max(12)
    private Short expirationMonth;

    @NotNull
    @Min(2026)
    private Short expirationYear;

    @NotBlank
    @Pattern(regexp = "\\d{3}", message = "CVC must be 3 digits")
    private String cvc;

    @NotNull
    private PaymentType type;

    private Boolean defaultPaymentmethod;

    @NotNull
    private Long accountId;

    public static PaymentMethodDTO convertToDTO(PaymentMethod entity) {
        if (entity == null) return null;
        return new PaymentMethodDTO(
                entity.getPaymentmethodId(),
                entity.getCardNumber(),
                entity.getExpirationMonth(),
                entity.getExpirationYear(),
                entity.getCvc(),
                entity.getType(),
                entity.getDefaultPaymentmethod(),
                entity.getAccount() != null ? entity.getAccount().getAccountId() : null
        );
    }
}