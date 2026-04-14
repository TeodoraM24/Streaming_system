package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Season;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeasonDTO {

    private Long seasonId;
    private String title;
    private LocalDate releasedate;
    private Long showId;

    public static SeasonDTO convertToDTO(Season entity) {
        if (entity == null) return null;
        return new SeasonDTO(
                entity.getSeasonId(),
                entity.getTitle(),
                entity.getReleasedate(),
                entity.getShow() != null ? entity.getShow().getShowsId() : null
        );
    }
}