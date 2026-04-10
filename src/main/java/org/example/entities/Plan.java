package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PlanDTO;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    private String name;
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", columnDefinition = "bpchar")
    private String currency;

    private Boolean active;

    @OneToMany(mappedBy = "plan")
    private List<Subscription> subscriptions;

    public Plan(PlanDTO dto) {
        if (dto != null) {
            this.planId = dto.getPlanId();
            this.name = dto.getName();
            this.description = dto.getDescription();
            this.price = dto.getPrice(); // Fixed: Direct assignment
            this.currency = dto.getCurrency();
            this.active = dto.getActive();
        }
    }
}