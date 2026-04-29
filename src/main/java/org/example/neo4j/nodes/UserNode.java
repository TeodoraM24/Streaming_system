package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {

    @Id
    private Long id;

    private String username;

    @Relationship(type = "HAS_PROFILE")
    private Set<ProfileNode> profiles = new HashSet<>();
}