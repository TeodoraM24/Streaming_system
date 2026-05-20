package org.example.mongo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "personnel")
public class PersonnelDocument {
    @Id
    private String id;

    private Long personnelId;
    private String name;
    private String roletype;
}