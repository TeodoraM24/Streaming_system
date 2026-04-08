package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Personnel;
import org.example.enums.PersonnelRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonnelDTO {

    private Long personnelId;
    private String name;
    private PersonnelRole roletype;

    // Static mapping method
    public static PersonnelDTO convertToDTO(Personnel entity) {
        if (entity == null) return null;
        return new PersonnelDTO(
                entity.getPersonnelId(),
                entity.getName(),
                entity.getRoletype()
        );
    }
}