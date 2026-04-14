package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Receipt;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private Long receiptId;
    private String receiptNumber;
    private BigDecimal price;
    private LocalDateTime paydate;
    private Long paymentId;

    public static ReceiptDTO convertToDTO(Receipt receipt) {
        if (receipt == null) return null;
        return new ReceiptDTO(
                receipt.getReceiptId(),
                receipt.getReceiptNumber(),
                receipt.getPrice(),
                receipt.getPaydate(),
                receipt.getPayment() != null ? receipt.getPayment().getPaymentId() : null
        );
    }
}