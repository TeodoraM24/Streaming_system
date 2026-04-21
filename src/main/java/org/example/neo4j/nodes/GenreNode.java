package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreNode {

    @Id
    private Long id;

    private String genrename;
}