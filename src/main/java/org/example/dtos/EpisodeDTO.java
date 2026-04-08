package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Episode;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeDTO {

    private Long episodeId;
    private String title;
    private String description;
    private LocalDate releasedate;
    private Short duration;
    private Long seasonId;

    public static EpisodeDTO convertToDTO(Episode episode) {
        if (episode == null) return null;
        return new EpisodeDTO(
                episode.getEpisodeId(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getReleasedate(),
                episode.getDuration(),
                episode.getSeason() != null ? episode.getSeason().getSeasonId() : null
        );
    }
}