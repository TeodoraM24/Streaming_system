package org.example.mongo.documents;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "plans")
public class PlanDocument {
    @Id
    private String id;

    private Long planId;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Boolean active;
}