package org.example.mongo.embedded;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEmbedded {
    private Long profileId;
    private String profilename;
    private Long accountId;

    private List<ListsEmbedded> lists;
    private List<ReviewEmbedded> reviews;
}