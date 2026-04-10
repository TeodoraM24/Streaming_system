package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String cardNumber;
    private Short expirationMonth;
    private Short expirationYear;
    private String cvc; // RESTORED
    private PaymentType type;
    private Boolean defaultPaymentmethod;

    public static PaymentMethodDTO convertToDTO(PaymentMethod entity) {
        if (entity == null) return null;
        return new PaymentMethodDTO(
                entity.getPaymentmethodId(),
                entity.getCardNumber(),
                entity.getExpirationMonth(),
                entity.getExpirationYear(),
                entity.getCvc(),
                entity.getType(),
                entity.getDefaultPaymentmethod()
        );
    }
}