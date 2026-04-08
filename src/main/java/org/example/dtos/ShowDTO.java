package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Show;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowDTO {

    private Integer showsId;
    private Long contentId;

    public static ShowDTO convertToDTO(Show entity) {
        if (entity == null) return null;
        return new ShowDTO(
                entity.getShowsId(),
                entity.getContent() != null ? entity.getContent().getContentId() : null
        );
    }
}