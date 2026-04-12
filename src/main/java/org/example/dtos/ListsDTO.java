package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Lists;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListsDTO {

    private Long listId;
    private String listname;
    private Long profileId;
    private List<Long> contentIds;

    public static ListsDTO convertToDTO(Lists entity) {
        if (entity == null) return null;

        List<Long> contentIds = entity.getContents() != null
                ? entity.getContents().stream().map(c -> c.getContentId()).toList()
                : null;

        return new ListsDTO(
                entity.getListId(),
                entity.getListname(),
                entity.getProfile() != null ? entity.getProfile().getProfileId() : null,
                contentIds
        );
    }
}