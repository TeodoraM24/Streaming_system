package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Node("Payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNode {

    @Id
    private Long id;

    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentdate;

    @Relationship(type = "USED_PAYMENT_METHOD")
    private PaymentMethodNode paymentmethod;

    @Relationship(type = "HAS_RECEIPT")
    private ReceiptNode receipt;
}