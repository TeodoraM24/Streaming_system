package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Profile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDTO {

    private Long profileId;
    private String profilename;
    private Long accountId;

    public static ProfileDTO convertToDTO(Profile entity) {
        if (entity == null) return null;
        return new ProfileDTO(
                entity.getProfileId(),
                entity.getProfilename(),
                entity.getAccount() != null ? entity.getAccount().getAccountId() : null
        );
    }
}