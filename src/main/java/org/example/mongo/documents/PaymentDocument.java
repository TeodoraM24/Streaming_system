package org.example.mongo.documents;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payments")
public class PaymentDocument {
    @Id
    private String id;

    private Long paymentId;
    private BigDecimal price;
    private String currency;
    private LocalDateTime createdAt;
    private String status;
    private Long subscriptionId;
    private Long paymentMethodId;
}