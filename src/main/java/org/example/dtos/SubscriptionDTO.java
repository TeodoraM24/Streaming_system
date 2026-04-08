package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Subscription;
import org.example.enums.SubscriptionStatus;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionDTO {

    private Long subscriptionId;
    private LocalDate startdate;
    private LocalDate enddate;
    private LocalDate nextBillDate;
    private SubscriptionStatus status;
    private Long accountId;
    private Long planId;

    public static SubscriptionDTO convertToDTO(Subscription entity) {
        if (entity == null) return null;
        return new SubscriptionDTO(
                entity.getSubscriptionId(),
                entity.getStartdate(),
                entity.getEnddate(),
                entity.getNextBillDate(),
                entity.getStatus(),
                entity.getAccount() != null ? entity.getAccount().getAccountId() : null,
                entity.getPlan() != null ? entity.getPlan().getPlanId() : null
        );
    }
}