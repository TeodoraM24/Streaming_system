package org.example.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ContentDTO;
import org.example.enums.ContentType;

@Entity
@Table(name = "content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    private String originaltitle;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal rating;

    private LocalDate releasedate;
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "content_type")
    private ContentType type;

    // This refers to the 'contents' field in the Lists entity which holds the @JoinTable
    @ManyToMany(mappedBy = "contents")
    private List<Lists> lists;

    @ManyToMany
    @JoinTable(
            name = "genre_has_content",
            joinColumns = @JoinColumn(name = "content_content_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_genre_id")
    )
    private List<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "content_has_personnel",
            joinColumns = @JoinColumn(name = "content_content_id"),
            inverseJoinColumns = @JoinColumn(name = "personnel_personnel_id")
    )
    private List<Personnel> personnel;

    // Ensure the 'Review' entity has a field named 'content'
    @OneToMany(mappedBy = "content")
    private List<Review> reviews;

    public Content(ContentDTO dto) {
        this.contentId = dto.getContentId();
        this.originaltitle = dto.getOriginaltitle();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.rating = dto.getRating();
        this.releasedate = dto.getReleasedate();
        this.thumbnail = dto.getThumbnail();
        this.type = dto.getType();
    }
}