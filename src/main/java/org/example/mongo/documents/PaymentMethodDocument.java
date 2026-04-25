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
@Document(collection = "paymentmethods")
public class PaymentMethodDocument {
    @Id
    private String id;

    private Long paymentmethodId;
    private String cardNumber;
    private Short expirationMonth;
    private Short expirationYear;
    private String cvc;
    private String type;
    private Boolean defaultPaymentmethod;
    private Long accountId;
}