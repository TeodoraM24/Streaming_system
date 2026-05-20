package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodEmbedded {
    private Long paymentmethodId;
    private String cardNumber;
    private Short expirationMonth;
    private Short expirationYear;
    private String cvc;
    private String type;
    private Boolean defaultPaymentmethod;
    private Long accountId;
}