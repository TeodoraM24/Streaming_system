package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.User;
import org.example.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private Long usersId;
    private String username;
    private Long accountId;
    private Role role;

    public static UserDTO convertToDTO(User entity) {
        if (entity == null) return null;
        return new UserDTO(
                entity.getUsersId(),
                entity.getUsername(),
                entity.getAccount() != null ? entity.getAccount().getAccountId() : null,
                entity.getRole()
        );
    }
}