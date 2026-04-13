package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Content;
import org.example.enums.ContentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentDTO {

    private Long contentId;
    private String originaltitle;
    private String title;
    private String description;
    private BigDecimal rating;
    private LocalDate releasedate;
    private String thumbnail;
    private ContentType type;

    private List<Long> genreIds;
    private List<Long> personnelIds;
    private Short duration;

    public static ContentDTO convertToDTO(Content content) {
        if (content == null) return null;

        List<Long> genreIds = content.getGenres() != null
                ? content.getGenres().stream().map(g -> g.getGenreId()).toList()
                : null;

        List<Long> personnelIds = content.getPersonnel() != null
                ? content.getPersonnel().stream().map(p -> p.getPersonnelId()).toList()
                : null;

        // ✅ FIX: get duration from Movie relation
        Short duration = null;
        if (content.getMovie() != null) {
            duration = content.getMovie().getDuration();
        }

        return new ContentDTO(
                content.getContentId(),
                content.getOriginaltitle(),
                content.getTitle(),
                content.getDescription(),
                content.getRating(),
                content.getReleasedate(),
                content.getThumbnail(),
                content.getType(),
                genreIds,
                personnelIds,
                duration
        );
    }
}