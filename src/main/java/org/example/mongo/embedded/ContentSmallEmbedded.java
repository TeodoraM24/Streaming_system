package org.example.mongo.embedded;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentSmallEmbedded {
    private Long contentId;
    private String title;
    private String type;
}