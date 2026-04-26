package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.ContentType;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Node("Movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieNode {

    @Id
    private Long id;

    private String originaltitle;
    private String title;
    private String description;
    private BigDecimal rating;
    private LocalDate releasedate;
    private String thumbnail;
    private ContentType type;
    private Short duration;

    @Relationship(type = "HAS_GENRE")
    private Set<GenreNode> genres = new HashSet<>();

    @Relationship(type = "HAS_PERSONNEL")
    private Set<PersonnelNode> personnel = new HashSet<>();
}