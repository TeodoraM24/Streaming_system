package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreEmbedded {
    private Long genreId;
    private String genrename;
}