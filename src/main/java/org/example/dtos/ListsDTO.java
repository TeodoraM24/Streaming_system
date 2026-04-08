package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Lists;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListsDTO {

    private Long listId;
    private String listname;
    private Long profileId;

    public static ListsDTO convertToDTO(Lists entity) {
        if (entity == null) return null;
        return new ListsDTO(
                entity.getListId(),
                entity.getListname(),
                entity.getProfile() != null ? entity.getProfile().getProfileId() : null
        );
    }
}