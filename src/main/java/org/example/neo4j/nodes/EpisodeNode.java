package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Episode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeNode {

    @Id
    private Long id;

    private String title;
    private Integer episodenumber;
    private Integer durationminutes;
}