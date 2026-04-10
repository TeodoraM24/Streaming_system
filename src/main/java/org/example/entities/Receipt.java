package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ReceiptDTO;
import java.math.BigDecimal; // Required for DECIMAL(10,2)
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // BigDecimal fix

    private LocalDateTime paydate;

    @OneToOne
    @JoinColumn(name = "payment_payment_id") // RESTORED
    private Payment payment;

    public Receipt(ReceiptDTO dto) {
        if (dto != null) {
            this.receiptId = dto.getReceiptId();
            this.receiptNumber = dto.getReceiptNumber();
            this.price = dto.getPrice();
            this.paydate = dto.getPaydate();
        }
    }
}