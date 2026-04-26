package org.example.mongo.documents;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reviews")
public class ReviewDocument {
    @Id
    private String id;

    private Long reviewId;
    private String title;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long profileId;
    private Long contentId;
}