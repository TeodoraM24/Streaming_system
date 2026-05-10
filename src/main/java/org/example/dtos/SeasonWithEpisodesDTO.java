package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Season;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeasonWithEpisodesDTO {

    private Long seasonId;
    private String title;
    private LocalDate releasedate;
    private Long showId;
    private List<EpisodeDTO> episodes;

    public static SeasonWithEpisodesDTO convertToDTO(Season entity) {
        if (entity == null) return null;
        return new SeasonWithEpisodesDTO(
                entity.getSeasonId(),
                entity.getTitle(),
                entity.getReleasedate(),
                entity.getShow() != null ? entity.getShow().getShowsId() : null,
                entity.getEpisodes() != null
                        ? entity.getEpisodes().stream().map(EpisodeDTO::convertToDTO).toList()
                        : List.of()
        );
    }
}
