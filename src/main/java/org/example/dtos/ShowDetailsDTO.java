package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Show;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowDetailsDTO {

    private Long showsId;
    private Long contentId;
    private List<SeasonWithEpisodesDTO> seasons;

    public static ShowDetailsDTO convertToDTO(Show entity) {
        if (entity == null) return null;
        return new ShowDetailsDTO(
                entity.getShowsId(),
                entity.getContent() != null ? entity.getContent().getContentId() : null,
                entity.getSeasons() != null
                        ? entity.getSeasons().stream().map(SeasonWithEpisodesDTO::convertToDTO).toList()
                        : List.of()
        );
    }
}
