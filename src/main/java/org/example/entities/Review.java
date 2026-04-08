package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private Integer reviewId;

    private String title;
    private Short rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "profile_profile_id")
    @JsonIgnore // Prevents loop back to Profile
    private Profile profile;

    @ManyToOne
    @JoinColumn(
            name = "content_content_id",
            nullable = false,
            columnDefinition = "INT"
    )
    @JsonIgnore // Prevents loop back to Content
    private Content content;

    public Review(ReviewDTO dto) {
        this.reviewId = dto.getReviewId();
        this.title = dto.getTitle();
        this.rating = dto.getRating();
        this.comment = dto.getComment();
        this.createdAt = dto.getCreatedAt();
    }
}