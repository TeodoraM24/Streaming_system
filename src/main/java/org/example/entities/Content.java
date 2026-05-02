package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ContentDTO;
import org.example.enums.ContentType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String originaltitle;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal rating;

    private LocalDate releasedate;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "content_type")
    private ContentType type;

    @ManyToMany(mappedBy = "contents")
    private List<Lists> lists;

    @ManyToMany
    @JoinTable(
            name = "genre_has_content",
            joinColumns = @JoinColumn(name = "content_content_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "content_has_personnel",
            joinColumns = @JoinColumn(name = "content_content_id"),
            inverseJoinColumns = @JoinColumn(name = "personnel_personnel_id")
    )
    private List<Personnel> personnel = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;


    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Movie movie;

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Show show;

    public Content(ContentDTO dto) {
        this.originaltitle = dto.getOriginaltitle();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.rating = dto.getRating();
        this.releasedate = dto.getReleasedate();
        this.thumbnail = dto.getThumbnail();
        this.type = dto.getType();
    }
}