package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.PlanDTO;

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
    private Double price;
    private String currency;
    private Boolean active;

    @OneToMany(mappedBy = "plan")
    private List<Subscription> subscriptions;

    // Conversion Constructor
    public Plan(PlanDTO dto) {
        this.planId = dto.getPlanId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.currency = dto.getCurrency();
        this.active = dto.getActive();
    }
}