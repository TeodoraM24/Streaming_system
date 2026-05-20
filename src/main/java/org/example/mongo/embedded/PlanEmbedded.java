package org.example.mongo.embedded;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEmbedded {
    private Long planId;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Boolean active;
}