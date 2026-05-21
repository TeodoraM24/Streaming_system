package org.example.mongo.embedded;

import lombok.*;
import org.example.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmbedded {

    private Long usersId;
    private String username;
    private String password;
    private Role role;
    private Long accountId;
}