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
@Document(collection = "receipts")
public class ReceiptDocument {
    @Id
    private String id;

    private Long receiptId;
    private String receiptNumber;
    private BigDecimal price;
    private LocalDateTime paydate;
    private Long paymentId;
}