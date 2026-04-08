package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PaymentDTO;
import org.example.enums.PaymentStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    private Double price;
    private String currency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "subscription_subscription_id")
    private Subscription subscription;

    @ManyToOne
    @JoinColumn(name = "paymentmethod_paymentmethod_id")
    private PaymentMethod paymentMethod;

    public Payment(PaymentDTO dto) {
        this.paymentId = dto.getPaymentId();
        this.price = dto.getPrice();
        this.currency = dto.getCurrency();
        this.createdAt = dto.getCreatedAt();
        this.status = dto.getStatus();
    }
}