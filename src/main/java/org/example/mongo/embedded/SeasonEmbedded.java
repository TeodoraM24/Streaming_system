package org.example.mongo.embedded;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonEmbedded {
    private Long seasonId;
    private String title;
    private LocalDate releasedate;
    private Long showId;

    private List<EpisodeEmbedded> episodes;
}