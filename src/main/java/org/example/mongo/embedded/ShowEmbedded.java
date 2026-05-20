package org.example.mongo.embedded;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowEmbedded {
    private Long showsId;
    private Long contentId;
    private String title;

    private List<SeasonEmbedded> seasons;
}