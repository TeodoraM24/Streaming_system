package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEmbedded {
    private Long movieId;
    private Short duration;
    private Long contentId;
    private String title;
}