package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListsNode {

    @Id
    private Long id;

    private String name;

    @Relationship(type = "CONTAINS_CONTENT")
    private Set<ContentNode> contents = new HashSet<>();
}