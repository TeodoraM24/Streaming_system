package org.example.mongo.embedded;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptEmbedded {
    private Long receiptId;
    private String receiptNumber;
    private BigDecimal price;
    private LocalDateTime paydate;
    private Long paymentId;
}