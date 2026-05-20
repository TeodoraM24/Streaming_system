package org.example.mongo.embedded;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEmbedded {
    private Long reviewId;
    private String title;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long profileId;
    private Long contentId;

    private ContentSmallEmbedded content;
}