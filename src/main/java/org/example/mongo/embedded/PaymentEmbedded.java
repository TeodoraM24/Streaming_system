package org.example.mongo.embedded;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEmbedded {
    private Long paymentId;
    private BigDecimal price;
    private String currency;
    private LocalDateTime createdAt;
    private String status;
    private Long subscriptionId;
    private Long paymentMethodId;

    private PaymentMethodEmbedded paymentMethod;
    private ReceiptEmbedded receipt;
}