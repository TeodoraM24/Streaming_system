package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Season")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeasonNode {

    @Id
    private Long id;

    private Integer seasonnumber;

    @Relationship(type = "HAS_EPISODE")
    private Set<EpisodeNode> episodes = new HashSet<>();
}