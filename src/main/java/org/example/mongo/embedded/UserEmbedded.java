package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmbedded {
    private Long usersId;
    private String username;
    private String password;
    private Long accountId;
}