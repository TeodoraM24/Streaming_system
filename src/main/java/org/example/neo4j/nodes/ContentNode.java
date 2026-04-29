package org.example.neo4j.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.ContentType;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;
import java.time.LocalDate;

@Node("Content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentNode {

    @Id
    private Long id;

    private String originaltitle;
    private String title;
    private String description;
    private BigDecimal rating;
    private LocalDate releasedate;
    private String thumbnail;
    private ContentType type;
}