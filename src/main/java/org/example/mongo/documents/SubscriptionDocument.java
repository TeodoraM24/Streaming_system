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
@Document(collection = "subscriptions")
public class SubscriptionDocument {
    @Id
    private String id;

    private Long subscriptionId;
    private LocalDate startdate;
    private LocalDate enddate;
    private LocalDate nextBillDate;
    private String status;
    private Long accountId;
    private Long planId;
}