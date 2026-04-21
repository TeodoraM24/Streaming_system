package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {

    @Id
    private Long id;

    private String username;
}