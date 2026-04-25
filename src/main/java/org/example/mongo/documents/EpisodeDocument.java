package org.example.mongo.documents;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "episodes")
public class EpisodeDocument {
    @Id
    private String id;

    private Long episodeId;
    private String title;
    private String description;
    private LocalDate releasedate;
    private Short duration;
    private Long seasonId;
}