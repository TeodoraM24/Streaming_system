package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Node("Review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewNode {

    @Id
    private Long id;

    private String title;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;

    @Relationship(type = "REVIEWS")
    private ContentNode content;
}