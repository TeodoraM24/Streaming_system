package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Review;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewDTO {

    private Integer reviewId;
    private String title;
    private Short rating;
    private String comment;
    private LocalDateTime createdAt;

    private Long profileId;
    private Long contentId;

    public static ReviewDTO convertToDTO(Review entity) {
        if (entity == null) return null;
        return new ReviewDTO(
                entity.getReviewId(),
                entity.getTitle(),
                entity.getRating(),
                entity.getComment(),
                entity.getCreatedAt(),
                entity.getProfile() != null ? entity.getProfile().getProfileId() : null,
                entity.getContent() != null ? entity.getContent().getContentId() : null
        );
    }
}