package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("Receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptNode {

    @Id
    private Long id;

    private String receiptnumber;
    private LocalDateTime createdat;
}