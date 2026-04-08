package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Receipt;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptDTO {

    private Long receiptId;
    private String receiptNumber;
    private Double price;
    private LocalDateTime paydate;
    private Long paymentId;

    public static ReceiptDTO convertToDTO(Receipt entity) {
        if (entity == null) return null;
        return new ReceiptDTO(
                entity.getReceiptId(),
                entity.getReceiptNumber(),
                entity.getPrice(),
                entity.getPaydate(),
                entity.getPayment() != null ? entity.getPayment().getPaymentId() : null
        );
    }
}