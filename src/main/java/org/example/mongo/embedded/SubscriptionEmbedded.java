package org.example.mongo.embedded;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEmbedded {
    private Long subscriptionId;
    private LocalDate startdate;
    private LocalDate enddate;
    private LocalDate nextBillDate;
    private String status;
    private Long accountId;
    private Long planId;

    private PlanEmbedded plan;
    private List<PaymentEmbedded> payments;
}