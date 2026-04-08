package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ReceiptDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    private String receiptNumber;
    private Double price;
    private LocalDateTime paydate;

    @OneToOne
    @JoinColumn(name = "payment_payment_id")
    private Payment payment;

    // Conversion Constructor
    public Receipt(ReceiptDTO dto) {
        this.receiptId = dto.getReceiptId();
        this.receiptNumber = dto.getReceiptNumber();
        this.price = dto.getPrice();
        this.paydate = dto.getPaydate();
    }
}