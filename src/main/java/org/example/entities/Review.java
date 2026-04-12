package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.ReviewDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "profile_profile_id", "content_content_id"
        }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private String title;

    @Min(1) @Max(5)
    private Short rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "profile_profile_id")
    @JsonIgnore
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "content_content_id")
    @JsonIgnore
    private Content content;

    public Review(ReviewDTO dto) {
        this.reviewId = dto.getReviewId();
        this.title = dto.getTitle();
        this.rating = dto.getRating();
        this.comment = dto.getComment();
        this.createdAt = dto.getCreatedAt();
    }
}