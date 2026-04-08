package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Payment;
import org.example.enums.PaymentStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {

    private Long paymentId;
    private Double price;
    private String currency;
    private LocalDateTime createdAt;
    private PaymentStatus status;

    private Long subscriptionId;
    private Long paymentMethodId;

    public static PaymentDTO convertToDTO(Payment payment) {
        if (payment == null) return null;
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getPrice(),
                payment.getCurrency(),
                payment.getCreatedAt(),
                payment.getStatus(),
                payment.getSubscription() != null ? payment.getSubscription().getSubscriptionId() : null,
                // Fixed: Changed getPaymentMethodId() to getPaymentmethodId()
                payment.getPaymentMethod() != null ? payment.getPaymentMethod().getPaymentmethodId() : null
        );
    }
}