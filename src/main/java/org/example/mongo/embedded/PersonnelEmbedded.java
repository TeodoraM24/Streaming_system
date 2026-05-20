package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelEmbedded {
    private Long personnelId;
    private String name;
    private String roletype;
}