package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;

@Node("Subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionNode {

    @Id
    private Long id;

    private LocalDate startdate;
    private LocalDate enddate;
    private String status;

    @Relationship(type = "HAS_PLAN")
    private PlanNode plan;

    @Relationship(type = "HAS_PAYMENT")
    private PaymentNode payment;
}