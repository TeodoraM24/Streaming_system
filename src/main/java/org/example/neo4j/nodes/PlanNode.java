package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;

@Node("Plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanNode {

    @Id
    private Long id;

    private String name;
    private BigDecimal price;
    private String description;
}