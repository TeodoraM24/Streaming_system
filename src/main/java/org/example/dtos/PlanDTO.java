package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Plan;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO {

    private Long planId;
    private String name;
    private String description;
    private Double price;
    private String currency;
    private Boolean active;

    // Static mapping method
    public static PlanDTO convertToDTO(Plan entity) {
        if (entity == null) return null;
        return new PlanDTO(
                entity.getPlanId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getActive()
        );
    }
}