package org.example.mongo.documents;

import lombok.*;
import org.example.mongo.embedded.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "content")
public class ContentDocument {
    @Id
    private String id;

    private Long contentId;
    private String originalTitle;
    private String title;
    private String description;
    private BigDecimal rating;
    private LocalDate releaseDate;
    private String thumbnail;
    private String type;

    private MovieEmbedded movie;
    private ShowEmbedded show;

    private List<GenreEmbedded> genres;
    private List<PersonnelEmbedded> personnel;
}