package org.example.mongo.embedded;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeEmbedded {
    private Long episodeId;
    private String title;
    private String description;
    private LocalDate releasedate;
    private Short duration;
    private Long seasonId;
}