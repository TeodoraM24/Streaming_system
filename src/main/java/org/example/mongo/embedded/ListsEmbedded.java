package org.example.mongo.embedded;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListsEmbedded {
    private Long listId;
    private String listname;
    private Long profileId;
    private List<Long> contentIds;

    private List<ContentSmallEmbedded> content;
}