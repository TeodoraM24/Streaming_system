package org.example.mongo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "genres")
public class GenreDocument {
    @Id
    private String id;

    private Long genreId;
    private String genrename;
}