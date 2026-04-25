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
@Document(collection = "contents")
public class ContentDocument {
    @Id
    private String id;

    private Long contentId;
    private String originaltitle;
    private String title;
    private String description;
    private BigDecimal rating;
    private LocalDate releasedate;
    private String thumbnail;
    private String type;

    private List<String> genres;
    private List<String> personnel;
}