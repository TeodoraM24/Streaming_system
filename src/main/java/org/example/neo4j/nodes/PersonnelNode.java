package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.PersonnelRole;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelNode {

    @Id
    private Long id;

    private String name;
    private PersonnelRole roletype;
}