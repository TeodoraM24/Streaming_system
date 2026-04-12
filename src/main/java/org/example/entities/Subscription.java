package org.example.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.SubscriptionDTO;
import org.example.enums.SubscriptionStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    private LocalDate startdate;
    private LocalDate enddate;

    @Column(name = "next_bill_date")
    private LocalDate nextBillDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "subscription_status")
    private SubscriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "accounts_account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "plan_plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "subscription")
    private List<Payment> payments;

    public Subscription(SubscriptionDTO dto) {
        this.subscriptionId = dto.getSubscriptionId();
        this.startdate = dto.getStartdate();
        this.enddate = dto.getEnddate();
        this.nextBillDate = dto.getNextBillDate();
        this.status = dto.getStatus();
    }
}