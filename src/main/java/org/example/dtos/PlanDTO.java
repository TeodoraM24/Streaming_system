package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Plan;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {
    private Long planId;
    private String name;
    private String description;
    private BigDecimal price; // BigDecimal for SQL DECIMAL support
    private String currency;
    private Boolean active;

    public static PlanDTO convertToDTO(Plan plan) {
        if (plan == null) return null;
        return new PlanDTO(
                plan.getPlanId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getActive()
        );
    }
}