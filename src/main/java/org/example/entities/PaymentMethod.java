package org.example.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PaymentMethodDTO;
import org.example.enums.PaymentType;

@Entity
@Table(name = "paymentmethod")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentmethod_id")
    private Long paymentmethodId;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "expiration_month")
    private Short expirationMonth;

    @Column(name = "expiration_year")
    private Short expirationYear;

    private String cvc;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(name = "default_paymentmethod")
    private Boolean defaultPaymentmethod;

    @OneToMany(mappedBy = "paymentMethod")
    private List<Payment> payments;

    public PaymentMethod(PaymentMethodDTO dto) {
        if (dto != null) {
            this.paymentmethodId = dto.getPaymentmethodId();
            this.cardNumber = dto.getCardNumber();
            this.expirationMonth = dto.getExpirationMonth();
            this.expirationYear = dto.getExpirationYear();
            this.cvc = dto.getCvc(); // RESTORED
            this.type = dto.getType();
            this.defaultPaymentmethod = dto.getDefaultPaymentmethod();
        }
    }
}